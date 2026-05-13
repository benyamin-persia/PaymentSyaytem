import { useState } from 'react'
import api from '../api/axiosInstance'
import AuthContext from './context'

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => { // Lazy initial state: reads localStorage only once when React creates the component.
    const token = localStorage.getItem('jwtToken') // Existing token means the browser already logged in earlier.
    return token ? { token } : null // Minimal user object: enough to know whether the user is authenticated.
  })

  async function login(username, password) {
    const response = await api.post('/api/auth/login', { username, password }) // Login endpoint returns the JWT from Spring Boot.
    const token = response.data.accessToken // Backend LoginResponse record exposes the token as accessToken.

    localStorage.setItem('jwtToken', token) // Save token so Axios can attach it to later API calls.
    setUser({ username, token }) // Update React state so protected pages know the user is logged in.
  }

  function logout() {
    localStorage.removeItem('jwtToken') // Remove token so future requests become anonymous.
    setUser(null) // Reset auth state so protected routes redirect to Login.
  }

  const value = { user, login, logout } // Context value exposes auth state plus actions to the rest of the app.

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
