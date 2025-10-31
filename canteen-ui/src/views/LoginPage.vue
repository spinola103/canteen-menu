<template>
  <div class="container" style="max-width:400px; margin-top:80px;">
    <h1>Login</h1>
    <div class="card">
      <label>College ID</label>
      <input class="input" v-model="collegeId" placeholder="e.g. CSE2023A45" />

      <label style="margin-top:8px;">Password / DOB</label>
      <input class="input" type="password" v-model="password" placeholder="e.g. 2005-04-10" />

      <button class="btn primary" style="margin-top:12px;" @click="login" :disabled="loading">
        {{ loading ? 'Logging in...' : 'Login' }}
      </button>

      <p v-if="error" style="color: var(--danger); margin-top:8px;">{{ error }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../api/http'

const router = useRouter()
const collegeId = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function login() {
  error.value = ''
  loading.value = true
  try {
    const { data } = await http.post('/api/auth/login', { collegeId: collegeId.value, password: password.value })
    localStorage.setItem('token', data.token)
    localStorage.setItem('role', data.role)
    localStorage.setItem('name', data.name)
    if (data.role === 'ADMIN') router.push('/admin')
    else router.push('/menu')
  } catch (e) {
    error.value = e?.response?.data?.message || 'Login failed'
  } finally {
    loading.value = false
  }
}
</script>
