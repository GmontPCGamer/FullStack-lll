import { useState, useEffect } from 'react';
import axios from 'axios';
import './index.css';

function App() {
  const [reportes, setReportes] = useState([]);
  const [alertas, setAlertas] = useState([]);
  
  const [nuevoReporte, setNuevoReporte] = useState({
    ubicacionLatitud: '',
    ubicacionLongitud: '',
    descripcion: ''
  });

  const BFF_URL = 'http://localhost:8080/api/bff';

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const resReportes = await axios.get(`${BFF_URL}/reportes`);
      setReportes(resReportes.data);
      
      const resAlertas = await axios.get(`${BFF_URL}/alertas`);
      setAlertas(resAlertas.data);
    } catch (error) {
      console.error("Error fetching data via BFF:", error);
    }
  };

  const handleInputChange = (e) => {
    setNuevoReporte({ ...nuevoReporte, [e.target.name]: e.target.value });
  };

  const submitReporte = async (e) => {
    e.preventDefault();
    try {
      await axios.post(`${BFF_URL}/reportes`, nuevoReporte);
      setNuevoReporte({ ubicacionLatitud: '', ubicacionLongitud: '', descripcion: '' });
      fetchData();
    } catch (error) {
      console.error("Error submitting reporte:", error);
    }
  };

  return (
    <div className="app-container">
      <h1>Valle del Sol - Prevención de Incendios</h1>
      <p>Plataforma Inteligente para la Gestión de Emergencias</p>

      <div className="grid">
        <div className="glass-card">
          <h2>Reportar Foco de Incendio</h2>
          <form onSubmit={submitReporte}>
            <div className="form-group">
              <input type="text" name="ubicacionLatitud" placeholder="Latitud (ej: -33.45)" value={nuevoReporte.ubicacionLatitud} onChange={handleInputChange} required />
            </div>
            <div className="form-group">
              <input type="text" name="ubicacionLongitud" placeholder="Longitud (ej: -70.66)" value={nuevoReporte.ubicacionLongitud} onChange={handleInputChange} required />
            </div>
            <div className="form-group">
              <textarea name="descripcion" placeholder="Descripción de la emergencia..." rows="4" value={nuevoReporte.descripcion} onChange={handleInputChange} required></textarea>
            </div>
            <button type="submit">Enviar Reporte</button>
          </form>
        </div>

        <div className="glass-card">
          <h2>Alertas a la Comunidad</h2>
          {alertas.length === 0 ? <p>No hay alertas activas.</p> : (
            <ul>
              {alertas.map(alerta => (
                <li key={alerta.id} style={{marginBottom: '1rem', borderBottom: '1px solid rgba(255,255,255,0.1)', paddingBottom: '1rem'}}>
                  <h3>{alerta.titulo} <span className="badge" style={{background: alerta.nivelGravedad === 'ALTO' ? '#ff4d4f' : '#faad14'}}>{alerta.nivelGravedad}</span></h3>
                  <p>{alerta.mensaje}</p>
                  <small>{new Date(alerta.fechaEmision).toLocaleString()}</small>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>

      <div className="glass-card" style={{marginTop: '2rem'}}>
        <h2>Monitoreo Geográfico (Reportes Recientes)</h2>
        <div style={{overflowX: 'auto'}}>
          <table style={{width: '100%', textAlign: 'left', borderCollapse: 'collapse'}}>
            <thead>
              <tr style={{borderBottom: '2px solid var(--primary)'}}>
                <th>ID</th>
                <th>Fecha</th>
                <th>Ubicación</th>
                <th>Descripción</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {reportes.map(rep => (
                <tr key={rep.id} style={{borderBottom: '1px solid rgba(255,255,255,0.1)'}}>
                  <td style={{padding: '10px'}}>{rep.id}</td>
                  <td style={{padding: '10px'}}>{new Date(rep.fechaReporte).toLocaleString()}</td>
                  <td style={{padding: '10px'}}>{rep.ubicacionLatitud}, {rep.ubicacionLongitud}</td>
                  <td style={{padding: '10px'}}>{rep.descripcion}</td>
                  <td style={{padding: '10px'}}><span className="badge">{rep.estado}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
          {reportes.length === 0 && <p style={{marginTop: '1rem'}}>No hay reportes de incendios.</p>}
        </div>
      </div>
    </div>
  );
}

export default App;
