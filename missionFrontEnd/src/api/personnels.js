import axios from "./axios"; // ou ton instance api existante

export const personnelApi = {
  getAll: () => axios.get("/personnels").then((r) => r.data),
  getOne: (id) => axios.get(`/personnels/${id}`).then((r) => r.data),
  create: (data) => axios.post("/personnels", data).then((r) => r.data),
  update: (id, data) => axios.put(`/personnels/${id}`, data).then((r) => r.data),
  remove: (id) => axios.delete(`/personnels/${id}`).then((r) => r.data),
};