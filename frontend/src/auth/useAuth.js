import { useContext } from 'react'
import AuthContext from './context'

export function useAuth() {
  const context = useContext(AuthContext) // Custom hook: gives components the auth state without passing props.

  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider') // Guard: catches pages rendered outside the provider.
  }

  return context
}
