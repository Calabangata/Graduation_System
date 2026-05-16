import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/users/students';

const studentService = {
  /**
   * Get total student count based on user role.
   * - ADMIN: Returns total count of all students
   * - TEACHER: Returns count of students supervised by this teacher
   */
  getTotalStudentCount: async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/count`, {
        withCredentials: true,
      });
      return response.data.count;
    } catch (error) {
      console.error('Error fetching student count:', error);
      throw error;
    }
  },

  /**
   * Get student list based on user role.
   * - ADMIN: Returns all students
   * - TEACHER: Returns students supervised by this teacher
   */
  getStudentList: async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/list`, {
        withCredentials: true,
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching student list:', error);
      throw error;
    }
  },
};

export default studentService;
