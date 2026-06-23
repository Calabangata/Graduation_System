import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/thesis-applications';

const thesisService = {
  /**
   * Get the authenticated student's active thesis application.
   * Returns the application data or throws on error.
   */
  getMyApplication: async () => {
    const response = await axios.get(`${API_BASE_URL}/my-application`, {
      withCredentials: true,
    });
    return response.data;
  },
};

export default thesisService;
