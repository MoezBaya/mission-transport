import axios from 'axios';

const axiosClient = axios.create({
  baseURL: 'http://localhost:8080/v1/api',
  headers: { 'Content-Type': 'application/json' },
});

//  AUTOMATIQUE : àjoute token dns requête
axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

//  AUTOMATIQUE : 401  logout
axiosClient.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  // laisse PrivateRoute gérer la redirection proprement
  window.dispatchEvent(new Event('auth:logout'));
}
    return Promise.reject(err);
  }
);

export default axiosClient;