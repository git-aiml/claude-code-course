import { useState, useEffect } from 'react'
import { StationProvider } from './contexts/StationContext'
import RadioPlayer from './components/RadioPlayer'
import StationSelector from './components/StationSelector'
import EnvironmentBadge from './components/EnvironmentBadge'
import './App.css'

function App() {
  const [backendHealth, setBackendHealth] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetch('/api/health')
      .then(res => res.json())
      .then(data => {
        setBackendHealth(data)
        setLoading(false)
      })
      .catch(error => {
        console.error('Backend health check failed:', error)
        setBackendHealth({ status: 'DOWN', error: error.message })
        setLoading(false)
      })
  }, [])

  return (
    <StationProvider>
      <EnvironmentBadge />
      <div className="app">
        <header className="app-header">
          <div className="header-content">
            <h1 className="app-title">radioawa</h1>
            <p className="app-tagline">Crystal-clear lossless audio streaming</p>
          </div>
          <StationSelector />
        </header>

        <main className="app-main">
          <RadioPlayer />

          <div className="backend-status">
            {loading ? (
              <span className="status-text">Checking backend...</span>
            ) : backendHealth?.status === 'UP' ? (
              <span className="status-text status-up">Backend connected: {backendHealth.service}</span>
            ) : (
              <span className="status-text status-down">Backend unavailable</span>
            )}
          </div>
        </main>

        <footer className="app-footer">
          <p>Ad-free • Data-free • Subscription-free</p>
          <p className="footer-note">Powered by React + Spring Boot</p>
        </footer>
      </div>
    </StationProvider>
  )
}

export default App
