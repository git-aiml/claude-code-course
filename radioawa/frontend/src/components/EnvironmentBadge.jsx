import { useState, useEffect } from 'react'
import './EnvironmentBadge.css'

function EnvironmentBadge() {
  const [envInfo, setEnvInfo] = useState(null)
  const [showDetails, setShowDetails] = useState(false)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchEnvironmentInfo = async () => {
      try {
        const response = await fetch('/api/environment')
        if (!response.ok) {
          throw new Error('Failed to fetch environment info')
        }
        const data = await response.json()
        setEnvInfo(data)
      } catch (error) {
        console.error('Error fetching environment info:', error)
        // Fallback to unknown if API fails
        setEnvInfo({ deploymentMode: 'unknown', activeProfile: 'unknown' })
      } finally {
        setLoading(false)
      }
    }

    fetchEnvironmentInfo()
  }, [])

  if (loading || !envInfo) {
    return null
  }

  const isDocker = envInfo.deploymentMode === 'docker'
  const icon = isDocker ? '🐳' : '💻'
  const label = isDocker ? 'Docker' : 'Local'
  const badgeClass = `env-badge ${isDocker ? 'docker' : 'local'}`

  const formatUptime = (seconds) => {
    const hours = Math.floor(seconds / 3600)
    const minutes = Math.floor((seconds % 3600) / 60)
    if (hours > 0) {
      return `${hours}h ${minutes}m`
    }
    return `${minutes}m`
  }

  return (
    <div className="env-badge-container">
      <div
        className={badgeClass}
        onClick={() => setShowDetails(!showDetails)}
        title="Click for details"
      >
        <span className="env-icon">{icon}</span>
        <span className="env-label">{label}</span>
      </div>

      {showDetails && (
        <div className="env-details">
          <div className="env-details-header">
            <span>Environment Info</span>
            <button
              className="env-details-close"
              onClick={() => setShowDetails(false)}
            >
              ×
            </button>
          </div>
          <div className="env-details-content">
            <div className="env-detail-item">
              <span className="env-detail-label">Deployment:</span>
              <span className="env-detail-value">{envInfo.deploymentMode}</span>
            </div>
            <div className="env-detail-item">
              <span className="env-detail-label">Profile:</span>
              <span className="env-detail-value">{envInfo.activeProfile}</span>
            </div>
            <div className="env-detail-item">
              <span className="env-detail-label">Version:</span>
              <span className="env-detail-value">{envInfo.version}</span>
            </div>
            <div className="env-detail-item">
              <span className="env-detail-label">Java:</span>
              <span className="env-detail-value">{envInfo.javaVersion}</span>
            </div>
            <div className="env-detail-item">
              <span className="env-detail-label">Uptime:</span>
              <span className="env-detail-value">{formatUptime(envInfo.uptime)}</span>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default EnvironmentBadge
