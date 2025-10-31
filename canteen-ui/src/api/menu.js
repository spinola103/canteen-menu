import http from './http'
import dayjs from 'dayjs'

export async function fetchMenu({ date, veg = null, mealType = null, q = '', page = 0, size = 8, sort = 'name,asc' }) {
  const params = new URLSearchParams()
  params.set('date', dayjs(date).format('YYYY-MM-DD'))
  if (veg !== null) params.set('veg', veg)
  if (mealType) params.set('mealType', mealType)
  if (q && q.trim()) params.set('q', q.trim())
  params.set('page', page)
  params.set('size', size)
  if (sort) params.set('sort', sort)
  const { data } = await http.get(`/api/menu?${params.toString()}`)
  return data // Page<MenuItemOut>
}

export async function fetchTop({ date, limit = 5 }) {
  const params = new URLSearchParams()
  if (date) params.set('date', dayjs(date).format('YYYY-MM-DD'))
  params.set('limit', limit)
  const { data } = await http.get(`/api/menu/top?${params.toString()}`)
  return data // List<MenuItemOut>
}
