<template>
  <div class="container">
    <h1>Admin Dashboard</h1>
    <button class="btn ghost" @click="logout">Logout</button>

    <!-- ======================= ADD MENU ITEM ======================= -->
    <div class="card" style="margin-top:16px;">
      <h2>Add Menu Item</h2>
      <div class="grid" style="margin-top:8px;">
        <div style="grid-column: span 3;"><input class="input" placeholder="Name" v-model="name" /></div>
        <div style="grid-column: span 3;"><input class="input" placeholder="Description" v-model="description" /></div>
        <div style="grid-column: span 3;"><input class="input" placeholder="Price" type="number" v-model.number="price" /></div>
        <div style="grid-column: span 3;">
          <select class="input" v-model="mealType">
            <option value="BREAKFAST">BREAKFAST</option>
            <option value="LUNCH">LUNCH</option>
            <option value="DINNER">DINNER</option>
          </select>
        </div>
      </div>

      <div class="row" style="margin-top:10px;">
        <label><input type="checkbox" v-model="veg" /> Veg</label>
        <div style="flex:1;">
          <input
            class="input"
            placeholder="Tags (comma-separated, e.g., spicy, south-indian)"
            v-model="tagsText"
          />
        </div>
        <button class="btn primary" @click="addItem">Add</button>
      </div>
      <p v-if="msg" style="margin-top:8px;">{{ msg }}</p>
    </div>

    <!-- ======================= MENU LIST WITH RATINGS ======================= -->
    <div class="card" style="margin-top:20px;">
      <h2>Menu Items & Ratings</h2>
      <button class="btn ghost" @click="loadItems">Reload</button>

      <div v-for="m in menu" :key="m.id" class="card" style="margin-top:10px;">
        <div class="row" style="justify-content:space-between;">
          <div style="flex:1;">
            <h3>{{ m.name }}</h3>
            <p>{{ m.description }}</p>
            <p>₹ {{ m.price }} - {{ m.mealType }} - {{ m.veg ? 'VEG' : 'NON-VEG' }}</p>
            <div v-if="m.tags?.length" class="row">
              <span v-for="t in m.tags" :key="t" class="badge">#{{ t }}</span>
            </div>
            <div v-if="m.avgStars">
              <p style="margin-top:6px;">⭐ Avg Rating: {{ m.avgStars.toFixed(1) }} ({{ m.count }} ratings)</p>
            </div>
          </div>
          <div class="row">
            <button class="btn ghost" @click="showRatings(m.id)">View Ratings</button>
            <button class="btn ghost" @click="remove(m.id)">Delete</button>
          </div>
        </div>

        <!-- Ratings -->
        <div v-if="ratingsMap[m.id]" class="card" style="background:#0e1633; margin-top:8px;">
          <h4>Ratings</h4>
          <div v-for="r in ratingsMap[m.id]" :key="r.id" class="card" style="margin-top:6px;">
            <p><b>{{ r.userId }}</b> — {{ r.stars }}★</p>
            <p>{{ r.comment || 'No comment' }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import http from '../api/http'
import dayjs from 'dayjs'

const name = ref('')
const description = ref('')
const price = ref(0)
const mealType = ref('LUNCH')
const veg = ref(true)
const tagsText = ref('')
const msg = ref('')
const menu = ref([])
const ratingsMap = ref({})

async function addItem() {
  msg.value = ''
  try {
    const tagsArray = tagsText.value
      .split(',')
      .map(t => t.trim())
      .filter(t => t.length > 0)

    const { data } = await http.post('/api/menu', {
      date: dayjs().format('YYYY-MM-DD'),
      mealType: mealType.value,
      name: name.value,
      description: description.value,
      price: price.value,
      veg: veg.value,
      tags: tagsArray
    })
    msg.value = `✅ Added "${data.name}" successfully`
    name.value = ''
    description.value = ''
    price.value = 0
    tagsText.value = ''
    loadItems()
  } catch (e) {
    msg.value = e?.response?.data?.message || 'Failed to add item'
  }
}

async function loadItems() {
  try {
    const { data } = await http.get('/api/menu')
    menu.value = data?.content ?? []
  } catch (e) {
    menu.value = []
  }
}

async function showRatings(menuId) {
  try {
    const { data } = await http.get(`/api/ratings/menu/${menuId}`)
    ratingsMap.value[menuId] = data
  } catch (e) {
    ratingsMap.value[menuId] = []
  }
}

async function remove(id) {
  if (!confirm('Are you sure you want to delete this item?')) return
  await http.delete(`/api/menu/${id}`)
  loadItems()
}

function logout() {
  localStorage.clear()
  window.location.href = '/login'
}

onMounted(loadItems)
</script>

<style scoped>
.badge {
  background: #18224a;
  color: #b0c4ff;
  font-size: 11px;
  border-radius: 6px;
  padding: 2px 6px;
}
</style>
