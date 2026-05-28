# 火车票务管理系统

前后端一体项目。

## 结构

- 后端：`src`
- 前端：`show`
- 启动脚本：`setup.bat`

## 启动

先确保本机已安装：

- JDK 17+
- Maven
- Node.js 18+
- npm

启动前端：

```bash
cd show
npm install
npm run dev
```

启动后端：

```bash
mvn spring-boot:run
```

也可以在项目根目录直接执行：

```bash
setup.bat
```

## 访问

- 前端：`http://localhost:3000`
- 后端：`http://localhost:8080`
- 前端通过 `/api` 代理到后端

## 功能

- 用户：登录、注册、余票查询、购票、订单、路线查询
- 管理：车次管理、票务发售与停售
