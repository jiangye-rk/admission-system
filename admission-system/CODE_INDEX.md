# 录取数据分析系统 - 方法说明与文件索引文档

> 本文档详细说明系统中各功能模块对应的代码文件位置，便于开发和维护。

---

## 一、项目结构概览

```
admission-system/
├── admission-backend/          # 后端项目 (SpringBoot)
│   ├── src/main/java/com/admission/
│   │   ├── controller/         # 控制器层 (API接口)
│   │   ├── service/            # 业务逻辑层
│   │   ├── mapper/             # 数据访问层
│   │   ├── entity/             # 实体类
│   │   ├── dto/                # 数据传输对象
│   │   ├── config/             # 配置类
│   │   ├── common/             # 通用工具类
│   │   └── utils/              # 工具类
│   └── src/main/resources/
│       └── mapper/             # MyBatis XML映射文件
├── admission-frontend/         # 前端项目 (Vue3)
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   ├── api/                # API接口封装
│   │   ├── router/             # 路由配置
│   │   └── store/              # 状态管理
│   └── vite.config.js          # Vite配置
└── sql/                        # 数据库脚本
```

---

## 二、功能模块与代码对应关系

### 2.1 用户认证模块

| 功能 | 后端代码 | 前端代码 | 说明 |
|------|----------|----------|------|
| 用户登录 | [UserController.login()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/UserController.java#L22-L37) | [Login.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Login.vue) | 验证用户名密码，返回JWT Token |
| 用户注册 | [UserController.register()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/UserController.java#L39-L84) | [Login.vue (注册Tab)](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Login.vue) | 包含考生信息验证 |
| Token生成 | [JwtUtil.generateToken()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/utils/JwtUtil.java) | - | JWT Token生成与解析 |
| Token验证 | [JwtInterceptor.preHandle()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/config/JwtInterceptor.java) | [request.js](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/api/request.js) | 拦截器验证Token有效性 |
| 获取用户信息 | [UserController.getUserProfile()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/UserController.java#L86-L102) | [user.js (Pinia Store)](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/store/user.js) | 获取当前登录用户详情 |

**相关Service方法：**
- [UserServiceImpl.login()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/UserServiceImpl.java#L48-L61) - 登录验证
- [UserServiceImpl.register()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/UserServiceImpl.java#L63-L109) - 用户注册

---

### 2.2 数据查询模块

| 功能 | 后端代码 | 前端代码 | 说明 |
|------|----------|----------|------|
| 基础查询 | [AdmissionDataController.query()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/AdmissionDataController.java#L27-L42) | [Query.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Query.vue) | 多条件组合查询 |
| 个性化查询 | [AdmissionDataController.queryWithUser()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/AdmissionDataController.java#L44-L62) | [Query.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Query.vue#L1-L100) | 结合用户分数位次查询 |
| 获取院校列表 | [AdmissionDataController.getSchools()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/AdmissionDataController.java#L76-L79) | [Query.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Query.vue#L1-L100) | 按年份获取院校 |
| 获取专业列表 | [AdmissionDataController.getMajors()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/AdmissionDataController.java#L81-L84) | [Query.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Query.vue#L1-L100) | 按院校+年份获取专业 |
| 分页查询院校 | [AdmissionDataController.getSchoolsPage()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/AdmissionDataController.java#L86-L94) | [Query.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Query.vue#L1-L100) | 支持关键词搜索 |
| 分页查询专业 | [AdmissionDataController.getMajorsPage()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/AdmissionDataController.java#L96-L104) | [Query.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Query.vue#L1-L100) | 支持关键词搜索 |
| 数据导出 | [AdmissionDataController.export()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/AdmissionDataController.java#L64-L74) | [Query.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Query.vue#L1-L100) | 导出选中数据为Excel |

**相关Service方法：**
- [AdmissionDataServiceImpl.queryPage()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/AdmissionDataServiceImpl.java#L29-L53) - 基础分页查询
- [AdmissionDataServiceImpl.queryPageWithUser()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/AdmissionDataServiceImpl.java#L55-L85) - 带用户信息的查询
- [AdmissionDataServiceImpl.getSchoolList()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/AdmissionDataServiceImpl.java#L87-L90) - 获取院校列表
- [AdmissionDataServiceImpl.getMajorList()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/AdmissionDataServiceImpl.java#L92-L95) - 获取专业列表

---

### 2.3 数据导入模块（管理员）

| 功能 | 后端代码 | 前端代码 | 说明 |
|------|----------|----------|------|
| Excel导入 | [ExcelController.importExcel()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/ExcelController.java#L28-L56) | [Import.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Import.vue) | 批量导入录取数据 |
| Excel数据转换 | [ExcelController.convertToEntity()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/ExcelController.java#L58-L115) | - | Excel行数据转实体 |
| 批量保存 | [AdmissionDataServiceImpl.importExcelData()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/AdmissionDataServiceImpl.java#L97-L108) | - | 批量入库 |

---

### 2.4 院校对比模块

| 功能 | 后端代码 | 前端代码 | 说明 |
|------|----------|----------|------|
| 院校对比分析 | [AdmissionDataController.compare()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/AdmissionDataController.java#L106-L113) | [Compare.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Compare.vue) | 多院校同专业对比 |
| 对比数据处理 | [AdmissionDataServiceImpl.compareSchools()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/AdmissionDataServiceImpl.java#L113-L156) | - | 生成ECharts数据格式 |
| 历年最低分对比 | [AdmissionDataController.schoolMinScore()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/AdmissionDataController.java#L115-L122) | [Visualization.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Visualization.vue) | 可视化分析页面 |

---

### 2.5 智能推荐模块

| 功能 | 后端代码 | 前端代码 | 说明 |
|------|----------|----------|------|
| 生成推荐 | [RecommendationController.generateRecommendations()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/RecommendationController.java#L30-L44) | [Recommendation.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Recommendation.vue) | 基于分数位次推荐 |
| 分页推荐 | [RecommendationController.getRecommendationsWithPage()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/RecommendationController.java#L46-L60) | [Recommendation.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Recommendation.vue) | 分页展示推荐结果 |
| 推荐历史 | [RecommendationController.getRecommendationHistory()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/RecommendationController.java#L62-L76) | [Recommendation.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Recommendation.vue) | 查看历史推荐记录 |
| 推荐统计 | [RecommendationController.getRecommendationStatistics()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/RecommendationController.java#L78-L91) | [Recommendation.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Recommendation.vue) | 推荐数据统计 |
| 院校推荐详情 | [RecommendationController.getSchoolRecommendations()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/RecommendationController.java#L93-L108) | [Recommendation.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Recommendation.vue) | 某院校推荐详情 |

**相关Service方法：**
- [RecommendationServiceImpl.generateRecommendations()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/RecommendationServiceImpl.java#L36-L85) - 核心推荐算法
- [RecommendationServiceImpl.calculateMatch()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/RecommendationServiceImpl.java#L87-L142) - 匹配度计算
- [RecommendationServiceImpl.saveRecommendationBatch()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/RecommendationServiceImpl.java#L144-L162) - 保存推荐记录

**推荐类型定义：**
- `TYPE_CHONG = 1` - 冲刺（分数略低于往年）
- `TYPE_WEN = 2` - 稳妥（分数与往年匹配）
- `TYPE_BAO = 3` - 保底（分数明显高于往年）

---

### 2.6 可视化分析模块

| 功能 | 后端代码 | 前端代码 | 说明 |
|------|----------|----------|------|
| 热门专业TOP N | [AdmissionDataController.getHotMajors()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/AdmissionDataController.java#L124-L128) | [Dashboard.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Dashboard.vue) | 首页热门专业展示 |
| 分数段分布 | [AdmissionDataController.getDistribution()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/AdmissionDataController.java#L130-L134) | [Dashboard.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Dashboard.vue) | 分数段统计图表 |
| 数据统计 | [AdmissionDataController.getStatistics()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/AdmissionDataController.java#L136-L140) | [Dashboard.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Dashboard.vue) | 首页统计卡片数据 |
| 省份热力图 | [AdmissionDataController.getProvinceHeatmap()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/AdmissionDataController.java#L142-L146) | [Visualization.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Visualization.vue) | 生源地分布 |

**相关Service方法：**
- [AdmissionDataServiceImpl.getHotMajors()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/AdmissionDataServiceImpl.java#L158-L161) - 热门专业统计
- [AdmissionDataServiceImpl.getScoreDistribution()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/AdmissionDataServiceImpl.java#L163-L190) - 分数段分布
- [AdmissionDataServiceImpl.getStatistics()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/AdmissionDataServiceImpl.java#L192-L210) - 综合统计

---

### 2.7 分数位次查询模块

| 功能 | 后端代码 | 前端代码 | 说明 |
|------|----------|----------|------|
| 分数查位次 | [ScoreSegmentController.getRank()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/ScoreSegmentController.java#L18-L24) | [Query.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Query.vue) | 根据分数查询位次 |
| 位次查询实现 | [ScoreSegmentServiceImpl.getRankByScore()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/ScoreSegmentServiceImpl.java) | - | 查询位次数据 |

---

### 2.8 用户管理模块（管理员）

| 功能 | 后端代码 | 前端代码 | 说明 |
|------|----------|----------|------|
| 用户列表 | [UserController.getUserList()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/UserController.java#L104-L109) | [UserManage.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/UserManage.vue) | 查看所有用户 |
| 重置密码 | [UserController.resetPassword()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/UserController.java#L111-L122) | [UserManage.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/UserManage.vue) | 管理员重置密码 |
| 更新状态 | [UserController.updateStatus()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/UserController.java#L124-L135) | [UserManage.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/UserManage.vue) | 启用/禁用用户 |

---

### 2.9 数据概览模块（Dashboard）

| 功能 | 后端代码 | 前端代码 | 说明 |
|------|----------|----------|------|
| 统计卡片 | [AdmissionDataController.getStatistics()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/AdmissionDataController.java#L136-L140) | [Dashboard.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Dashboard.vue#L1-L100) | 院校/专业/录取人数/平均分 |
| 热门专业图表 | [AdmissionDataController.getHotMajors()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/AdmissionDataController.java#L124-L128) | [Dashboard.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Dashboard.vue#L1-L100) | TOP10热门专业柱状图 |
| 分数分布图表 | [AdmissionDataController.getDistribution()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/controller/AdmissionDataController.java#L130-L134) | [Dashboard.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Dashboard.vue#L1-L100) | 分数段分布折线图 |

---

## 三、核心实体类说明

### 3.1 数据库实体

| 实体类 | 文件路径 | 说明 | 对应数据表 |
|--------|----------|------|------------|
| User | [User.java](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/entity/User.java) | 用户实体 | user |
| AdmissionData | [AdmissionData.java](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/entity/AdmissionData.java) | 录取数据实体 | admission_data |
| RecommendationRecord | [RecommendationRecord.java](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/entity/RecommendationRecord.java) | 推荐记录实体 | recommendation_record |
| ScoreSegment | [ScoreSegment.java](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/entity/ScoreSegment.java) | 分数段位次实体 | score_segment |

### 3.2 DTO对象

| DTO类 | 文件路径 | 说明 |
|-------|----------|------|
| RecommendationRequest | [RecommendationRequest.java](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/dto/RecommendationRequest.java) | 推荐请求参数 |
| RecommendationResult | [RecommendationResult.java](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/dto/RecommendationResult.java) | 推荐结果数据 |

---

## 四、配置文件说明

### 4.1 后端配置

| 配置文件 | 路径 | 说明 |
|----------|------|------|
| application.yml | [application.yml](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/resources/application.yml) | 主配置文件（数据库、Redis、端口） |
| MybatisPlusConfig | [MybatisPlusConfig.java](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/config/MybatisPlusConfig.java) | MyBatis-Plus分页配置 |
| WebConfig | [WebConfig.java](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/config/WebConfig.java) | Web配置（拦截器、跨域） |
| JwtInterceptor | [JwtInterceptor.java](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/config/JwtInterceptor.java) | JWT验证拦截器 |
| RedisConfig | [RedisConfig.java](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/config/RedisConfig.java) | Redis配置 |

### 4.2 前端配置

| 配置文件 | 路径 | 说明 |
|----------|------|------|
| vite.config.js | [vite.config.js](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/vite.config.js) | Vite构建配置、代理设置 |
| router/index.js | [index.js](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/router/index.js) | Vue Router路由配置 |
| store/user.js | [user.js](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/store/user.js) | Pinia用户状态管理 |
| api/request.js | [request.js](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/api/request.js) | Axios请求封装 |
| api/api.js | [api.js](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/api/api.js) | API接口定义 |

---

## 五、前端页面路由对照

| 页面功能 | 路由路径 | 组件文件 | 权限 |
|----------|----------|----------|------|
| 登录页 | /login | [Login.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Login.vue) | 公开 |
| 数据概览 | /dashboard | [Dashboard.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Dashboard.vue) | 登录用户 |
| 数据查询 | /query | [Query.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Query.vue) | 登录用户 |
| 院校对比 | /compare | [Compare.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Compare.vue) | 登录用户 |
| 可视化分析 | /visualization | [Visualization.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Visualization.vue) | 登录用户 |
| 智能推荐 | /recommendation | [Recommendation.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Recommendation.vue) | 登录用户 |
| 数据导入 | /import | [Import.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/Import.vue) | 仅管理员 |
| 用户管理 | /users | [UserManage.vue](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/src/views/UserManage.vue) | 仅管理员 |

---

## 六、API接口列表

### 6.1 用户相关 (/api/user)

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/user/login | POST | 用户登录 |
| /api/user/register | POST | 用户注册 |
| /api/user/info | GET | 获取当前用户信息 |
| /api/user/profile | GET | 获取用户详细资料 |
| /api/user/list | GET | 获取用户列表（管理员） |
| /api/user/resetPassword | POST | 重置密码（管理员） |
| /api/user/updateStatus | POST | 更新用户状态（管理员） |

### 6.2 数据查询相关 (/api/data)

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/data/query | GET | 基础数据查询 |
| /api/data/queryWithUser | GET | 个性化数据查询 |
| /api/data/schools | GET | 获取院校列表 |
| /api/data/majors | GET | 获取专业列表 |
| /api/data/schools/page | GET | 分页获取院校 |
| /api/data/majors/page | GET | 分页获取专业 |
| /api/data/export | POST | 导出数据 |
| /api/data/compare | POST | 院校对比分析 |
| /api/data/hot | GET | 热门专业TOP N |
| /api/data/distribution | GET | 分数段分布 |
| /api/data/statistics | GET | 数据统计 |
| /api/data/province | GET | 省份热力图数据 |
| /api/data/schoolMinScore | POST | 院校历年最低分 |

### 6.3 Excel导入 (/api/excel)

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/excel/import | POST | Excel数据导入 |

### 6.4 分数位次 (/api/score)

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/score/rank | GET | 分数查询位次 |

### 6.5 智能推荐 (/api/recommendation)

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/recommendation/generate | POST | 生成推荐 |
| /api/recommendation/page | POST | 分页获取推荐 |
| /api/recommendation/history | GET | 推荐历史 |
| /api/recommendation/statistics | GET | 推荐统计 |
| /api/recommendation/school/{yxdm} | GET | 院校推荐详情 |
| /api/recommendation/probability | GET | 录取概率分析 |

---

## 七、数据库表结构

| 表名 | 说明 | SQL文件 |
|------|------|---------|
| user | 用户表 | [admission_db_complete.sql](file:///d:/Documents/dev/addmission-system2.0/admission-system/sql/admission_db_complete.sql) |
| admission_data | 录取数据表 | [admission_db_complete.sql](file:///d:/Documents/dev/addmission-system2.0/admission-system/sql/admission_db_complete.sql) |
| recommendation_record | 推荐记录表 | [recommendation_table.sql](file:///d:/Documents/dev/addmission-system2.0/admission-system/sql/recommendation_table.sql) |
| score_segment | 分数段位次表 | [admission_db_complete.sql](file:///d:/Documents/dev/addmission-system2.0/admission-system/sql/admission_db_complete.sql) |

---

## 八、关键算法说明

### 8.1 推荐算法逻辑

位置：[RecommendationServiceImpl.calculateMatch()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/RecommendationServiceImpl.java#L87-L142)

```
用户分数 - 专业最低分 >= 20 或 用户位次 - 专业最低位次 >= 5000
  → 保底 (TYPE_BAO)

用户分数 - 专业最低分 >= 0 或 用户位次 - 专业最低位次 >= 0
  → 稳妥 (TYPE_WEN)

用户分数 - 专业最低分 >= -15 或 用户位次 - 专业最低位次 >= -3000
  → 冲刺 (TYPE_CHONG)

其他情况 → 不推荐
```

### 8.2 选科匹配逻辑

位置：[AdmissionDataServiceImpl.filterByUserSubjects()](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/src/main/java/com/admission/service/impl/AdmissionDataServiceImpl.java)

- 用户选考科目与专业选考要求进行匹配
- 支持部分匹配和完全匹配模式

---

## 九、部署相关

| 文件 | 路径 | 说明 |
|------|------|------|
| docker-compose.yml | [docker-compose.yml](file:///d:/Documents/dev/addmission-system2.0/admission-system/docker-compose.yml) | Docker Compose配置 |
| Dockerfile (后端) | [Dockerfile](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-backend/Dockerfile) | 后端Docker镜像 |
| Dockerfile (前端) | [Dockerfile](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/Dockerfile) | 前端Docker镜像 |
| nginx.conf | [nginx.conf](file:///d:/Documents/dev/addmission-system2.0/admission-system/admission-frontend/nginx.conf) | Nginx配置 |
| docker-deploy.bat | [docker-deploy.bat](file:///d:/Documents/dev/addmission-system2.0/admission-system/docker-deploy.bat) | Windows部署脚本 |
| docker-deploy.sh | [docker-deploy.sh](file:///d:/Documents/dev/addmission-system2.0/admission-system/docker-deploy.sh) | Linux部署脚本 |
| start-without-docker.bat | [start-without-docker.bat](file:///d:/Documents/dev/addmission-system2.0/admission-system/start-without-docker.bat) | 无Docker启动脚本 |

---

## 十、文档索引

| 文档 | 路径 | 说明 |
|------|------|------|
| Docker部署说明 | [DOCKER_README.md](file:///d:/Documents/dev/addmission-system2.0/admission-system/DOCKER_README.md) | Docker方式部署指南 |
| 非Docker部署说明 | [NO_DOCKER_README.md](file:///d:/Documents/dev/addmission-system2.0/admission-system/NO_DOCKER_README.md) | 传统方式部署指南 |
| 毕业设计说明书 | [毕业设计说明书(定稿).docx](file:///d:/Documents/dev/addmission-system2.0/admission-system/毕业设计说明书(定稿).docx) | 完整设计文档 |
| 数据库ER图 | [database_er.png](file:///d:/Documents/dev/addmission-system2.0/admission-system/images/database_er.png) | 数据库关系图 |
| 功能模块图 | [function_module.png](file:///d:/Documents/dev/addmission-system2.0/admission-system/images/function_module.png) | 系统功能模块 |
| 系统架构图 | [system_architecture.png](file:///d:/Documents/dev/addmission-system2.0/admission-system/images/system_architecture.png) | 技术架构图 |

---

**文档生成时间：** 2026-05-04

**系统版本：** v2.0
