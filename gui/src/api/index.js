import axios from 'axios';

export const api = axios.create({
  baseURL: 'http://127.0.0.1:8090/v1',
  // this header needs to be set to do post requests ¯\_(ツ)_/¯
  headers: {
    'Content-Type': 'text/plain'
  }
});
