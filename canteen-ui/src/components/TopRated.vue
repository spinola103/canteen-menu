<template>
  <div class="card" v-if="items.length">
    <div class="row" style="justify-content: space-between;">
      <h2>Top Rated Today</h2>
      <button class="btn ghost" @click="$emit('reload')">Reload</button>
    </div>
    <div class="row" style="margin-top:8px; overflow-x:auto; gap:10px;">
      <div v-for="i in items" :key="i.id" class="pill" @click="$emit('pick', i)">
        <div style="font-weight:700;">{{ i.name }}</div>
        <div class="row">
          <span class="badge">{{ i.mealType }}</span>
          <span class="badge" v-if="i.veg">VEG</span>
          <span class="badge" v-else>NON-VEG</span>
          <span class="badge">{{ i.avgStars.toFixed(1) }}★ ({{ i.count }})</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ items: { type: Array, default: () => [] } })
defineEmits(['reload','pick'])
</script>

<style scoped>
.pill {
  min-width: 220px; padding: 10px 12px; cursor: pointer;
  border-radius: 12px; border: 1px solid rgba(255,255,255,0.08);
  background: #0e1633;
}
.pill:hover { filter: brightness(1.07); }
</style>
