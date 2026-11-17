import axiosClient from './axiosClient';

export async function login(email, motDePasse) {
  const { data } = await axiosClient.post('/login', { email, motDePasse });
  return data;
}

export async function logout() {
  await axiosClient.post('/logout');
  localStorage.removeItem('token');
  localStorage.removeItem('user');
}

export async function changerMotDePasse(ancien, nouveau, confirmation) {
  try {
    const res = await axiosClient.put("/users/me/password", {
      ancienMotDePasse: ancien,
      nouveauMotDePasse: nouveau,
      confirmationMotDePasse: confirmation,
    });
    return res.data;
  } catch (err) {
    // Renvoie le message du backend au composant
    throw new Error(err.response?.data || "Erreur inconnue");
  }
}