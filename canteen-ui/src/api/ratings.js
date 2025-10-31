// src/api/ratings.js
import http from './http'

export async function createRating({ menuItemId, userId, stars, comment }) {
  const { data } = await http.post('/api/ratings', { menuItemId, userId, stars, comment })
  return data
}
