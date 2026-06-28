import { render, screen, waitFor } from '@testing-library/react'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'
import App from './App'

vi.mock('axios')

const mockReportes = [
  {
    id: 1,
    ubicacionLatitud: '-33.4569',
    ubicacionLongitud: '-70.6483',
    descripcion: 'Foco de incendio en sector norte',
    estado: 'REPORTADO',
    fechaReporte: '2026-06-27T12:00:00'
  }
]

const mockAlertas = [
  {
    id: 1,
    titulo: 'Riesgo de Incendio',
    mensaje: 'Evacúe el sector',
    nivelGravedad: 'ALTO',
    fechaEmision: '2026-06-27T12:00:00'
  }
]

describe('App Component', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    axios.get.mockResolvedValueOnce({ data: mockReportes })
    axios.get.mockResolvedValueOnce({ data: mockAlertas })
  })

  it('renderiza el título de la aplicación', async () => {
    render(<App />)
    expect(screen.getByText('Municipalidad Valle del Sol')).toBeInTheDocument()
  })

  it('muestra el estado de carga inicialmente', () => {
    render(<App />)
    expect(screen.getByText('Conectando con los microservicios...')).toBeInTheDocument()
  })

  it('renderiza datos después de la carga', async () => {
    render(<App />)
    await waitFor(() => {
      expect(screen.getByText(/Foco de incendio en sector norte/)).toBeInTheDocument()
    })
    expect(screen.getByText('Riesgo de Incendio')).toBeInTheDocument()
  })

  it('muestra estadísticas de reportes', async () => {
    render(<App />)
    await waitFor(() => {
      expect(screen.getByText('Reportes Totales')).toBeInTheDocument()
    })
  })

  it('muestra mensaje de error cuando falla la conexión', async () => {
    axios.get.mockReset()
    axios.get.mockRejectedValue(new Error('Network Error'))
    render(<App />)
    await waitFor(() => {
      expect(screen.getByText(/No se pudo conectar con el servidor/)).toBeInTheDocument()
    })
  })

  it('renderiza el formulario de reporte', async () => {
    render(<App />)
    await waitFor(() => {
      expect(screen.getByText(/Reportar Foco de Incendio/)).toBeInTheDocument()
    })
  })

  it('renderiza el formulario de alerta', async () => {
    render(<App />)
    await waitFor(() => {
      expect(screen.getByText(/Emitir Alerta a la Comunidad/)).toBeInTheDocument()
    })
  })
})
