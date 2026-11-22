import axiosClient from "./axiosClient";

export const personnelApi = {
  getAll: () => axiosClient.get("/personnel").then((response) => response.data),
  create: (data) => axiosClient.post("/personnel", data).then((response) => response.data),
  update: (id, data) => axiosClient.put(`/personnel/${id}`, data).then((response) => response.data),
  remove: (id) => axiosClient.delete(`/personnel/${id}`).then((response) => response.data),
};

