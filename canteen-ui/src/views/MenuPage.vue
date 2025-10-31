<template>
  <div class="container">
    <!-- ================= HEADER ================= -->
    <div class="row" style="justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <h1>Canteen Menu</h1>
      <div class="row" style="gap: 10px;">
        <span class="badge">Welcome, {{ userName }}</span>
        <button class="btn ghost" @click="logout">Logout</button>
      </div>
    </div>

    <!-- ================= FILTERS ================= -->
    <MenuFilters
      :date="date"
      :mealType="mealType"
      :veg="veg"
      :q="q"
      :sort="sort"
      @update:date="v => (date = v)"
      @update:mealType="v => (mealType = v)"
      @update:veg="v => (veg = v)"
      @update:q="v => (q = v)"
      @update:sort="v => (sort = v)"
      @reset="resetFilters"
    />

    <!-- ================= TOP RATED ================= -->
    <TopRated :items="top" @reload="loadTop" @pick="openRate" />

    <!-- ================= FILTER STATUS + RELOAD ================= -->
    <div style="margin: 10px 0;" class="row">
      <button class="btn" @click="reload">Reload</button>
      <span class="badge">Date: {{ date }}</span>
      <span class="badge" v-if="mealType">Meal: {{ mealType }}</span>
      <span class="badge" v-if="veg !== null">Veg: {{ veg ? 'Yes' : 'No' }}</span>
      <span class="badge" v-if="q">Search: "{{ q }}"</span>
      <span class="badge">Sort: {{ sort }}</span>
    </div>

    <!-- ================= MENU ITEMS ================= -->
    <div v-if="loading" class="card">Loading…</div>
    <div v-else-if="items.length === 0" class="card">No menu items found.</div>
    <div v-else class="grid" style="margin-top: 10px;">
      <div v-for="item in items" :key="item.id" style="grid-column: span 6;">
        <MenuCard :item="item" @rate="openRate" />
      </div>
    </div>

    <!-- ================= PAGINATION ================= -->
    <Pagination :page="page" @update:page="v => { page = v; reload() }" />

    <!-- ================= RATING MODAL ================= -->
    <RatingModal :open="modalOpen" :item="selected" @close="modalOpen = false" @submitted="reload" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import dayjs from 'dayjs'
import { fetchMenu, fetchTop } from '../api/menu'
import MenuFilters from '../components/MenuFilters.vue'
import MenuCard from '../components/MenuCard.vue'
import RatingModal from '../components/RatingModal.vue'
import Pagination from '../components/Pagination.vue'
import TopRated from '../components/TopRated.vue'

const date = ref(dayjs().format('YYYY-MM-DD'))
const mealType = ref('')
const veg = ref(null) // null = any
const q = ref('')
const sort = ref('name,asc')

const page = ref(0)
const size = 8
const loading = ref(false)
const items = ref([])
const totalPages = ref(0)
const modalOpen = ref(false)
const selected = ref(null)
const top = ref([])

const userName = ref(localStorage.getItem('name') || 'Guest')

function logout() {
  localStorage.clear()
  window.location.href = '/login'
}

function resetFilters() {
  date.value = dayjs().format('YYYY-MM-DD')
  mealType.value = ''
  veg.value = null
  q.value = ''
  sort.value = 'name,asc'
  page.value = 0
  reload()
  loadTop()
}

async function reload() {
  loading.value = true
  try {
    const data = await fetchMenu({
      date: date.value,
      mealType: mealType.value || null,
      veg: veg.value,
      q: q.value,
      page: page.value,
      size,
      sort: sort.value
    })
    items.value = Array.isArray(data) ? data : data.content ?? []
    totalPages.value = data?.totalPages ?? 0
  } catch (e) {
    console.error(e)
    items.value = []
    totalPages.value = 0
  } finally {
    loading.value = false
  }
}

async function loadTop() {
  try {
    top.value = await fetchTop({ date: date.value, limit: 6 })
  } catch (e) {
    top.value = []
  }
}

function openRate(item) {
  selected.value = item
  modalOpen.value = true
}

watch([date, mealType, veg, q, sort], () => {
  page.value = 0
  reload()
}, { deep: true })

onMounted(() => {
  reload()
  loadTop()
})
</script>
