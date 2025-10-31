<template>
  <div class="row" style="justify-content:center; gap:6px; margin:12px 0;">
    <button class="btn" :disabled="page===0" @click="$emit('update:page', page-1)">Prev</button>
    <span class="badge">Page {{ page+1 }}</span>
    <button class="btn" @click="$emit('update:page', page+1)">Next</button>
  </div>
</template>

<script setup>
defineProps({ page: { type: Number, default: 0 } })
</script>
