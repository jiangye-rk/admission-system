import request from './request'

export const userApi = {
  login: (data) => request.post('/user/login', data),
  register: (data) => request.post('/user/register', data),
  getInfo: () => request.get('/user/info'),
  getProfile: () => request.get('/user/profile'),
  getUserList: () => request.get('/user/list'),
  resetPassword: (data) => request.post('/user/resetPassword', data),
  updateStatus: (data) => request.post('/user/updateStatus', data)
}

export const dataApi = {
  query: (params) => request.get('/data/query', { params }),
  queryWithUser: (params) => request.get('/data/queryWithUser', { params }),
  getSchools: (year) => request.get('/data/schools', { params: { year } }),
  getMajors: (yxdm, year) => request.get('/data/majors', { params: { yxdm, year } }),
  compare: (data) => request.post('/data/compare', data),
  getHotMajors: (year, limit = 10) => request.get('/data/hot', { params: { year, limit } }),
  getDistribution: (year) => request.get('/data/distribution', { params: { year } }),
  getProvinceHeatmap: (year) => request.get('/data/province', { params: { year } }),
  getStatistics: (year) => request.get('/data/statistics', { params: { year } }),
  schoolMinScore: (data) => request.post('/data/schoolMinScore', data),
  export: (ids) => request.post('/data/export', ids, { responseType: 'blob' })
}

export const excelApi = {
  import: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/excel/import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export const scoreApi = {
  getRank: (score) => request.get('/score/rank', { params: { score } })
}