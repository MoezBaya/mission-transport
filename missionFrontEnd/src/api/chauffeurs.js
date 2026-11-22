import axiosClient from "./axiosClient";

export const chauffeurApi = {
  getAll: () => axiosClient.get("/chauffeur").then((response) => response.data),
  create: (data) => axiosClient.post("/chauffeur", data).then((response) => response.data),
  update: (id, data) => axiosClient.put(`/chauffeur/${id}`, data).then((response) => response.data),
  remove: (id) => axiosClient.delete(`/chauffeur/${id}`).then((response) => response.data),
};
