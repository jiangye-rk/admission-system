import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const role = ref(localStorage.getItem('role') || '')
  
  // 考生信息
  const ksxm = ref(localStorage.getItem('ksxm') || '')
  const ksh = ref(localStorage.getItem('ksh') || '')
  const fs = ref(localStorage.getItem('fs') || '')
  const wc = ref(localStorage.getItem('wc') || '')
  const xk = ref(localStorage.getItem('xk') || '')
  const gzxx = ref(localStorage.getItem('gzxx') || '')
  
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'admin')
  
  const setUserInfo = (data) => {
    token.value = data.token
    username.value = data.username
    role.value = data.role
    localStorage.setItem('token', data.token)
    localStorage.setItem('username', data.username)
    localStorage.setItem('role', data.role)
    
    // 保存考生信息
    if (data.ksxm) {
      ksxm.value = data.ksxm
      localStorage.setItem('ksxm', data.ksxm)
    }
    if (data.ksh) {
      ksh.value = data.ksh
      localStorage.setItem('ksh', data.ksh)
    }
    if (data.fs) {
      fs.value = data.fs
      localStorage.setItem('fs', data.fs)
    }
    if (data.wc) {
      wc.value = data.wc
      localStorage.setItem('wc', data.wc)
    }
    if (data.xk) {
      xk.value = data.xk
      localStorage.setItem('xk', data.xk)
    }
    if (data.gzxx) {
      gzxx.value = data.gzxx
      localStorage.setItem('gzxx', data.gzxx)
    }
  }
  
  const clearUserInfo = () => {
    token.value = ''
    username.value = ''
    role.value = ''
    ksxm.value = ''
    ksh.value = ''
    fs.value = ''
    wc.value = ''
    xk.value = ''
    gzxx.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    localStorage.removeItem('ksxm')
    localStorage.removeItem('ksh')
    localStorage.removeItem('fs')
    localStorage.removeItem('wc')
    localStorage.removeItem('xk')
    localStorage.removeItem('gzxx')
  }
  
  return {
    token,
    username,
    role,
    ksxm,
    ksh,
    fs,
    wc,
    xk,
    gzxx,
    isLoggedIn,
    isAdmin,
    setUserInfo,
    clearUserInfo
  }
})
