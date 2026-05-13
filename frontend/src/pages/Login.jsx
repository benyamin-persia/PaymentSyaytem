import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'

function Login() {
  const [username, setUsername] = useState('') // Controlled input: React state is the source of truth for this field.
  const [password, setPassword] = useState('') // Controlled input: every typed character updates this state.
  const [error, setError] = useState('') // Error state lets the page show failed login feedback.
  const [isSubmitting, setIsSubmitting] = useState(false) // Loading state prevents duplicate login clicks.
  const { login } = useAuth() // AuthContext owns the real API login call.
  const navigate = useNavigate() // useNavigate changes route after successful login without reloading the page.

  async function handleSubmit(event) {
    event.preventDefault() // Prevent browser form reload so React can handle the login.
    setError('') // Clear old error before trying the new credentials.
    setIsSubmitting(true) // Disable the button while the request is running.

    try {
      await login(username, password) // Calls POST /api/auth/login through AuthContext.
      navigate('/customers') // Move the authenticated user to the protected customers page.
    } catch {
      setError('Invalid username or password.') // Keep the message simple and avoid leaking security details.
    } finally {
      setIsSubmitting(false) // Re-enable the button after success or failure.
    }
  }

  return (
    <main className="auth-page">
      <section className="card auth-card">
        <p className="eyebrow">JD Bank Frontend Lab</p>
        <h1>Login</h1>
        <p className="muted">Sign in with your Spring Boot account to load customers.</p>

        <form className="form" onSubmit={handleSubmit}>
          <label htmlFor="username">Username or email</label>
          <input
            id="username"
            name="username"
            type="text"
            value={username}
            onChange={(event) => setUsername(event.target.value)} // onChange keeps state synced with the input value.
            autoComplete="username"
            required
          />

          <label htmlFor="password">Password</label>
          <input
            id="password"
            name="password"
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)} // onChange makes this a controlled password input.
            autoComplete="current-password"
            required
          />

          {error && <p className="error-message">{error}</p>}

          <button type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Signing in...' : 'Sign in'}
          </button>
        </form>
      </section>
    </main>
  )
}

export default Login
