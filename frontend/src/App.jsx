import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider, useAuth } from './auth/AuthContext'
import Customers from './pages/Customers'
import Login from './pages/Login'

function RequireAuth({ children }) {
  const { user } = useAuth() // Protected route reads global auth state from AuthContext.

  return user ? children : <Navigate to="/login" replace /> // Navigate redirects direct /customers visits when no token exists.
}

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route
            path="/customers"
            element={
              <RequireAuth>
                <Customers />
              </RequireAuth>
            }
          />
          <Route path="*" element={<Navigate to="/login" replace />} /> {/* Catch-all route sends unknown URLs to Login. */}
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}

export default App
