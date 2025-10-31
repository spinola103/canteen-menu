<template>
  <div class="modal-backdrop" v-if="open">
    <div class="modal">
      <div class="row" style="justify-content: space-between; margin-bottom:8px;">
        <h2>Rate: {{ item?.name }}</h2>
        <button class="btn ghost" @click="$emit('close')">✕</button>
      </div>

      <div class="row" style="margin: 8px 0;">
        <span
          v-for="i in 5"
          :key="i"
          class="star"
          style="font-size:24px; cursor:pointer;"
          @click="stars = i"
        >
          {{ i <= stars ? '★' : '☆' }}
        </span>
      </div>

      <div style="margin-top:10px;">
        <label>Comment</label>
        <textarea
          class="input"
          rows="3"
          v-model="comment"
          placeholder="How was it?"
        ></textarea>
      </div>

      <div class="row" style="justify-content: flex-end; margin-top:14px;">
        <button class="btn ghost" @click="$emit('close')">Cancel</button>
        <button class="btn primary" :disabled="submitting || stars===0" @click="submit">
          {{ submitting ? 'Submitting...' : 'Submit Rating' }}
        </button>
      </div>

      <p v-if="error" style="color: var(--danger); margin-top:8px;">{{ error }}</p>
      <p v-if="ok" style="color: var(--accent); margin-top:8px;">Thanks for rating!</p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { createRating } from '../api/ratings'

const props = defineProps({
  open: Boolean,
  item: Object
})
const emits = defineEmits(['close', 'submitted'])

const stars = ref(0)
const comment = ref('')
const error = ref('')
const ok = ref(false)
const submitting = ref(false)

watch(
  () => props.open,
  (o) => {
    if (o) {
      stars.value = 0
      comment.value = ''
      error.value = ''
      ok.value = false
    }
  }
)

async function submit() {
  if (!props.item) return
  error.value = ''
  ok.value = false
  submitting.value = true

  try {
    const userId = localStorage.getItem('name') || 'anonymous'
    await createRating({
      menuItemId: props.item.id,
      userId,
      stars: stars.value,
      comment: comment.value.trim()
    })
    ok.value = true
    emits('submitted')
    setTimeout(() => emits('close'), 400)
  } catch (e) {
    const msg = e?.response?.data?.message || 'Failed to submit rating'
    error.value = msg
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
label {
  display: block;
  font-size: 12px;
  color: var(--muted);
  margin-bottom: 6px;
}
</style>
