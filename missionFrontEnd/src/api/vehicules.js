import axiosClient from "./axiosClient";

export const vehiculeApi = {
  getAll: () => axiosClient.get("/vehicule").then((response) => response.data),
  create: (data) => axiosClient.post("/vehicule", data).then((response) => response.data),
  update: (id, data) => axiosClient.put(`/vehicule/${id}`, data).then((response) => response.data),
  remove: (id) => axiosClient.delete(`/vehicule/${id}`).then((response) => response.data),
};
