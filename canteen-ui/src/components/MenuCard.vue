<template>
  <div class="card">
    <div class="row" style="justify-content: space-between;">
      <div>
        <div class="row">
          <span class="badge">{{ item.mealType }}</span>
          <span class="badge" v-if="item.veg">VEG</span>
          <span class="badge" v-else>NON-VEG</span>
        </div>
        <h2 style="margin-top:6px; color:#fff; font-size:20px;">{{ item.name }}</h2>
        <p style="margin:6px 0; color: var(--muted)">{{ item.description || '—' }}</p>
            <div v-if="item.tags?.length" class="row" style="margin-top:4px;">
            <span v-for="t in item.tags" :key="t" class="badge">#{{ t }}</span>
            </div>
      </div>
      <div style="text-align:right;">
        <div style="font-size:22px; font-weight:800;">₹ {{ item.price }}</div>
        <div style="margin-top:6px;">
          <span v-for="i in 5" :key="i" class="star">
            {{ i <= Math.round(item.avgStars) ? '★' : '☆' }}
          </span>
          <span class="badge" style="margin-left:6px;">{{ item.avgStars.toFixed(1) }} ({{ item.count }})</span>
        </div>
      </div>
    </div>

    <hr>
    <div class="row" style="justify-content: space-between;">
      <div class="row">
        <span style="font-size:12px; color: var(--muted)">ID: {{ item.id }}</span>
      </div>
      <button class="btn" @click="$emit('rate', item)">Rate this dish</button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  item: { type: Object, required: true }
})
</script>
