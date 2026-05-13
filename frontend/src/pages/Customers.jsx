import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axiosInstance'
import { useAuth } from '../auth/useAuth'

function formatCreatedAt(createdAt) {
  return createdAt ? new Date(createdAt).toLocaleString() : '-' // Format timestamps only when the API sends one.
}

function Customers() {
  const [customers, setCustomers] = useState([]) // Array state stores the API result for rendering.
  const [error, setError] = useState('') // Error state shows a friendly message if loading fails.
  const [isLoading, setIsLoading] = useState(true) // Loading state tells the user data is being fetched.
  const { logout } = useAuth() // AuthContext owns token cleanup.
  const navigate = useNavigate() // Used after logout to return to Login.

  useEffect(() => { // useEffect runs side effects after React renders the component.
    let isActive = true // Guard prevents state updates if the component unmounts before the request finishes.

    async function loadCustomers() {
      try {
        const response = await api.get('/api/customers') // Axios interceptor attaches Authorization automatically.

        if (!isActive) {
          return // Stop if the page unmounted before the API response arrived.
        }

        setCustomers(response.data) // Store API data in state so React re-renders the table.
        setError('')
      } catch {
        if (!isActive) {
          return // Avoid updating state after React has removed this component.
        }

        setError('Could not load customers. Make sure Spring Boot is running and you are logged in.')
      }

      setIsLoading(false) // Stop the loading message after success or failure.
    }

    loadCustomers() // Start the API request when the page opens.

    return () => {
      isActive = false // Cleanup runs before unmount, preventing stale async updates.
    }
  }, []) // Empty dependency array means the request runs once when the component mounts.

  function handleLogout() {
    logout() // Removes the JWT token and clears auth state.
    navigate('/login') // Send user back to Login after clearing auth.
  }

  return (
    <main className="dashboard-page">
      <section className="dashboard-header">
        <div>
          <p className="eyebrow">Secure Customer Area</p>
          <h1>Customers</h1>
          <p className="muted">This table is loaded from GET /api/customers with a Bearer JWT.</p>
        </div>
        <button className="secondary-button" type="button" onClick={handleLogout}>
          Logout
        </button>
      </section>

      <section className="card">
        {isLoading && <p className="muted">Loading customers...</p>}
        {error && <p className="error-message">{error}</p>}

        {!isLoading && !error && (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>First name</th>
                  <th>Last name</th>
                  <th>Email</th>
                  <th>Created</th>
                </tr>
              </thead>
              <tbody>
                {customers.map((customer) => (
                  <tr key={customer.id}>
                    <td>{customer.id}</td>
                    <td>{customer.firstName}</td>
                    <td>{customer.lastName}</td>
                    <td>{customer.email}</td>
                    <td>{formatCreatedAt(customer.createdAt)}</td>
                  </tr>
                ))}
              </tbody>
            </table>

            {customers.length === 0 && <p className="empty-state">No customers found.</p>}
          </div>
        )}
      </section>
    </main>
  )
}

export default Customers
