import { useState, useEffect } from 'react'
import './App.css'

interface Alert {
  id: number
  title: string
  description: string
  severity: string
  status: string
  sourceIp?: string
  destinationIp?: string
  createdAt: string
  updatedAt: string
}

function App() {
  const [alerts, setAlerts] = useState<Alert[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchAlerts()
  }, [])

  const fetchAlerts = async () => {
    try {
      setLoading(true)
      const response = await fetch('http://localhost:8080/api/alerts')
      if (!response.ok) {
        throw new Error('Failed to fetch alerts')
      }
      const data = await response.json()
      setAlerts(data)
      setError(null)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred')
    } finally {
      setLoading(false)
    }
  }

  const getSeverityColor = (severity: string) => {
    switch (severity) {
      case 'HIGH':
        return '#ff4444'
      case 'MEDIUM':
        return '#ffaa00'
      case 'LOW':
        return '#00ff00'
      default:
        return '#888888'
    }
  }

  return (
    <div className="app">
      <header>
        <h1>AI SOC Copilot</h1>
        <p>Enterprise Security Operations Center</p>
      </header>

      <main>
        <div className="alerts-header">
          <h2>Security Alerts</h2>
          <button onClick={fetchAlerts} disabled={loading}>
            {loading ? 'Loading...' : 'Refresh'}
          </button>
        </div>

        {error && (
          <div className="error-message">
            Error: {error}
          </div>
        )}

        {loading ? (
          <div className="loading">Loading alerts...</div>
        ) : alerts.length === 0 ? (
          <div className="no-alerts">No alerts found</div>
        ) : (
          <div className="alerts-grid">
            {alerts.map((alert) => (
              <div key={alert.id} className="alert-card">
                <div className="alert-header">
                  <h3>{alert.title}</h3>
                  <span
                    className="severity-badge"
                    style={{ backgroundColor: getSeverityColor(alert.severity) }}
                  >
                    {alert.severity}
                  </span>
                </div>
                <p className="alert-description">{alert.description}</p>
                <div className="alert-details">
                  {alert.sourceIp && (
                    <div>
                      <strong>Source IP:</strong> {alert.sourceIp}
                    </div>
                  )}
                  {alert.destinationIp && (
                    <div>
                      <strong>Destination IP:</strong> {alert.destinationIp}
                    </div>
                  )}
                  <div>
                    <strong>Status:</strong> {alert.status}
                  </div>
                  <div>
                    <strong>Created:</strong>{' '}
                    {new Date(alert.createdAt).toLocaleString()}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  )
}

export default App
