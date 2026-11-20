import axiosClient from "./axiosClient";

export const personnelApi = {
  getAll: () => axiosClient.get("/personnels").then((r) => r.data),
  getOne: (id) => axiosClient.get(`/personnels/${id}`).then((r) => r.data),
  create: (data) => axiosClient.post("/personnels", data).then((r) => r.data),
  update: (id, data) => axiosClient.put(`/personnels/${id}`, data).then((r) => r.data),
  remove: (id) => axiosClient.delete(`/personnels/${id}`).then((r) => r.data),
}; // ou ton instance api existante

