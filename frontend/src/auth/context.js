import { createContext } from 'react'

const AuthContext = createContext(null) // Shared Context object: provider writes auth data and hook reads it.

export default AuthContext
