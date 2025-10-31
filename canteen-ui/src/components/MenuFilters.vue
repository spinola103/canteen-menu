<template>
  <div class="card">
    <div class="row" style="justify-content: space-between;">
      <h2>Filters</h2>
      <button class="btn ghost" @click="$emit('reset')">Reset</button>
    </div>

    <div class="grid" style="margin-top:8px;">
      <div style="grid-column: span 3;">
        <label>Date</label>
        <input class="input" type="date" :value="date" @input="$emit('update:date', $event.target.value)" />
      </div>

      <div style="grid-column: span 3;">
        <label>Meal Type</label>
        <select class="input" :value="mealType" @change="$emit('update:mealType', $event.target.value)">
          <option value="">All</option>
          <option value="BREAKFAST">BREAKFAST</option>
          <option value="LUNCH">LUNCH</option>
          <option value="DINNER">DINNER</option>
        </select>
      </div>

      <div style="grid-column: span 3;">
        <label>Veg</label>
        <select class="input" :value="vegString" @change="$emit('update:veg', parseBool($event.target.value))">
          <option value="any">Any</option>
          <option value="true">Veg Only</option>
          <option value="false">Non-Veg Only</option>
        </select>
      </div>

      <div style="grid-column: span 3;">
        <label>Search</label>
        <input class="input" :value="q" placeholder="name / description / tag" @input="$emit('update:q', $event.target.value)" />
      </div>

      <div style="grid-column: span 3;">
        <label>Sort</label>
        <select class="input" :value="sort" @change="$emit('update:sort', $event.target.value)">
          <option value="name,asc">Name ↑</option>
          <option value="name,desc">Name ↓</option>
          <option value="price,asc">Price ↑</option>
          <option value="price,desc">Price ↓</option>
        </select>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({
  date: String,
  mealType: String,
  veg: { type: [Boolean, null], default: null },
  q: String,
  sort: String
})
const vegString = computed(() => props.veg === null ? 'any' : String(props.veg))
function parseBool(v) { return v === 'any' ? null : v === 'true' }
</script>

<style scoped>
label { display:block; font-size:12px; color: var(--muted); margin-bottom:6px; }
</style>
