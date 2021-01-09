import axios from 'axios';

const request = axios.create();

const baseURL = 'http://localhost:8080';

export const api = (options = {}) => {
  return request({
    baseURL,
    ...options,
    headers: {
      ...options.headers,
    },
  });
};
