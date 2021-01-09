import { api } from './Api';

export const fetchAllBooks = (keyword) => {
  return api({
    method: 'get',
    url: '/books',
    params: {
      keyword,
    },
  });
};
