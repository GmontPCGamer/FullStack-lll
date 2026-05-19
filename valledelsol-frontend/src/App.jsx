import { useState, useEffect } from 'react';
import axios from 'axios';
import './index.css';

// Usa proxy de Vite en dev → no necesitamos URL absoluta
const BFF_URL = '/api/bff';

function App() {
  const [reportes, setReportes] = useState([]);
  const [alertas, setAlertas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [submitStatus, setSubmitStatus] = useState(null);

  const [nuevoReporte, setNuevoReporte] = useState({
    ubicacionLatitud: '',
    ubicacionLongitud: '',
    descripcion: ''
  });

  const [nuevaAlerta, setNuevaAlerta] = useState({
    titulo: '',
    mensaje: '',
    nivelGravedad: 'MEDIO'
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [resReportes, resAlertas] = await Promise.all([
        axios.get(`${BFF_URL}/reportes`),
        axios.get(`${BFF_URL}/alertas`)
      ]);
      setReportes(Array.isArray(resReportes.data) ? resReportes.data : []);
      setAlertas(Array.isArray(resAlertas.data) ? resAlertas.data : []);
    } catch (err) {
      setError('No se pudo conectar con el servidor. Asegúrate de que los microservicios estén corriendo.');
      console.error('Error fetching data via BFF:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleReporteChange = (e) => {
    setNuevoReporte({ ...nuevoReporte, [e.target.name]: e.target.value });
  };

  const handleAlertaChange = (e) => {
    setNuevaAlerta({ ...nuevaAlerta, [e.target.name]: e.target.value });
  };

  const submitReporte = async (e) => {
    e.preventDefault();
    setSubmitStatus(null);
    try {
      await axios.post(`${BFF_URL}/reportes`, nuevoReporte, {
        headers: { 'Content-Type': 'application/json' }
      });
      setNuevoReporte({ ubicacionLatitud: '', ubicacionLongitud: '', descripcion: '' });
      setSubmitStatus({ type: 'success', msg: '✅ Reporte enviado exitosamente.' });
      fetchData();
    } catch (err) {
      setSubmitStatus({ type: 'error', msg: '❌ Error al enviar el reporte. Verifica la conexión.' });
      console.error('Error submitting reporte:', err);
    }
  };

  const submitAlerta = async (e) => {
    e.preventDefault();
    setSubmitStatus(null);
    try {
      await axios.post(`${BFF_URL}/alertas`, nuevaAlerta, {
        headers: { 'Content-Type': 'application/json' }
      });
      setNuevaAlerta({ titulo: '', mensaje: '', nivelGravedad: 'MEDIO' });
      setSubmitStatus({ type: 'success', msg: '✅ Alerta creada exitosamente.' });
      fetchData();
    } catch (err) {
      setSubmitStatus({ type: 'error', msg: '❌ Error al crear la alerta. Verifica la conexión.' });
      console.error('Error submitting alerta:', err);
    }
  };

  const gravedad = (nivel) => {
    const colores = { ALTO: '#ff4d4f', MEDIO: '#faad14', BAJO: '#52c41a' };
    return colores[nivel] || '#aaa';
  };

  const estadoColor = (estado) => {
    const colores = { REPORTADO: '#faad14', EN_REVISION: '#1890ff', CONTROLADO: '#52c41a' };
    return colores[estado] || '#aaa';
  };

  return (
    <div className="app-container">
      {/* Header */}
      <header className="app-header">
        <div className="header-content">
          <div className="header-icon">🔥</div>
          <div>
            <h1>Municipalidad Valle del Sol</h1>
            <p className="subtitle">Plataforma Inteligente de Prevención y Gestión de Incendios</p>
          </div>
        </div>
        <button className="refresh-btn" onClick={fetchData} title="Actualizar datos">
          🔄 Actualizar
        </button>
      </header>

      {/* Estado de conexión */}
      {error && (
        <div className="alert-banner error">
          ⚠️ {error}
        </div>
      )}
      {submitStatus && (
        <div className={`alert-banner ${submitStatus.type}`}>
          {submitStatus.msg}
        </div>
      )}

      {loading ? (
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Conectando con los microservicios...</p>
        </div>
      ) : (
        <>
          {/* Estadísticas rápidas */}
          <div className="stats-row">
            <div className="stat-card">
              <span className="stat-number">{reportes.length}</span>
              <span className="stat-label">Reportes Totales</span>
            </div>
            <div className="stat-card">
              <span className="stat-number">{reportes.filter(r => r.estado === 'REPORTADO').length}</span>
              <span className="stat-label">Activos</span>
            </div>
            <div className="stat-card">
              <span className="stat-number">{alertas.length}</span>
              <span className="stat-label">Alertas Emitidas</span>
            </div>
            <div className="stat-card danger">
              <span className="stat-number">{alertas.filter(a => a.nivelGravedad === 'ALTO').length}</span>
              <span className="stat-label">Nivel ALTO</span>
            </div>
          </div>

          {/* Formularios */}
          <div className="grid">
            {/* Reportar incendio */}
            <div className="glass-card">
              <h2>🗺️ Reportar Foco de Incendio</h2>
              <form onSubmit={submitReporte}>
                <div className="form-group">
                  <label>Latitud</label>
                  <input
                    type="text"
                    name="ubicacionLatitud"
                    placeholder="Ej: -33.4569"
                    value={nuevoReporte.ubicacionLatitud}
                    onChange={handleReporteChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Longitud</label>
                  <input
                    type="text"
                    name="ubicacionLongitud"
                    placeholder="Ej: -70.6483"
                    value={nuevoReporte.ubicacionLongitud}
                    onChange={handleReporteChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Descripción de la Emergencia</label>
                  <textarea
                    name="descripcion"
                    placeholder="Describe el foco de incendio: extensión, materiales afectados, riesgo estimado..."
                    rows="4"
                    value={nuevoReporte.descripcion}
                    onChange={handleReporteChange}
                    required
                  />
                </div>
                <button type="submit" className="btn-primary">🚨 Enviar Reporte</button>
              </form>
            </div>

            {/* Emitir alerta */}
            <div className="glass-card">
              <h2>📢 Emitir Alerta a la Comunidad</h2>
              <form onSubmit={submitAlerta}>
                <div className="form-group">
                  <label>Título de la Alerta</label>
                  <input
                    type="text"
                    name="titulo"
                    placeholder="Ej: Riesgo de Incendio en Sector Norte"
                    value={nuevaAlerta.titulo}
                    onChange={handleAlertaChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Mensaje</label>
                  <textarea
                    name="mensaje"
                    placeholder="Instrucciones y detalles para la comunidad..."
                    rows="4"
                    value={nuevaAlerta.mensaje}
                    onChange={handleAlertaChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Nivel de Gravedad</label>
                  <select name="nivelGravedad" value={nuevaAlerta.nivelGravedad} onChange={handleAlertaChange}>
                    <option value="BAJO">🟢 BAJO</option>
                    <option value="MEDIO">🟡 MEDIO</option>
                    <option value="ALTO">🔴 ALTO</option>
                  </select>
                </div>
                <button type="submit" className="btn-warning">📣 Publicar Alerta</button>
              </form>
            </div>
          </div>

          {/* Alertas activas */}
          <div className="glass-card" style={{ marginTop: '2rem' }}>
            <h2>🔔 Alertas Activas ({alertas.length})</h2>
            {alertas.length === 0 ? (
              <p className="empty-msg">✅ No hay alertas activas en este momento.</p>
            ) : (
              <div className="alertas-list">
                {alertas.map(alerta => (
                  <div key={alerta.id} className="alerta-item">
                    <div className="alerta-header">
                      <h3>{alerta.titulo}</h3>
                      <span className="badge" style={{ background: gravedad(alerta.nivelGravedad) }}>
                        {alerta.nivelGravedad}
                      </span>
                    </div>
                    <p>{alerta.mensaje}</p>
                    <small>📅 {alerta.fechaEmision ? new Date(alerta.fechaEmision).toLocaleString('es-CL') : 'Sin fecha'}</small>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Tabla de reportes */}
          <div className="glass-card" style={{ marginTop: '2rem' }}>
            <h2>📋 Monitoreo de Reportes ({reportes.length})</h2>
            {reportes.length === 0 ? (
              <p className="empty-msg">No hay reportes de incendios registrados.</p>
            ) : (
              <div style={{ overflowX: 'auto' }}>
                <table className="reportes-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Fecha</th>
                      <th>Coordenadas</th>
                      <th>Descripción</th>
                      <th>Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    {reportes.map(rep => (
                      <tr key={rep.id}>
                        <td>#{rep.id}</td>
                        <td>{rep.fechaReporte ? new Date(rep.fechaReporte).toLocaleString('es-CL') : '-'}</td>
                        <td>
                          <code>{rep.ubicacionLatitud}, {rep.ubicacionLongitud}</code>
                        </td>
                        <td>{rep.descripcion}</td>
                        <td>
                          <span className="badge" style={{ background: estadoColor(rep.estado) }}>
                            {rep.estado}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </>
      )}

      <footer className="app-footer">
        <p>🏛️ Municipalidad Valle del Sol &copy; {new Date().getFullYear()} — Sistema de Prevención de Incendios</p>
      </footer>
    </div>
  );
}

export default App;
