import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080', // Base URL: every request starts from the Spring Boot server.
})

api.interceptors.request.use((config) => { // Request interceptor: runs before Axios sends each request.
  const token = localStorage.getItem('jwtToken') // localStorage keeps the JWT after login so refresh does not immediately lose it.

  if (token) {
    config.headers.Authorization = `Bearer ${token}` // Bearer header: Spring Security reads this JWT in JwtAuthenticationFilter.
  }

  return config // Axios must receive the config back after we edit it.
})

api.interceptors.response.use(
  (response) => response, // Successful responses pass through unchanged.
  (error) => {
    if (error.response?.status === 401 && window.location.pathname !== '/login') { // 401 means the token is missing, expired, or invalid.
      localStorage.removeItem('jwtToken') // Clear the bad token so the next request does not reuse it.
      window.location.href = '/login' // Redirect to login because the user must authenticate again.
    }

    return Promise.reject(error) // Keep the error available to the page that made the request.
  },
)

export default api
