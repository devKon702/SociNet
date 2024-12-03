FROM node:18-alpine AS build
WORKDIR /usr/src/app
COPY package*.json ./
RUN npm ci
COPY . .

FROM node:18-alpine as production
WORKDIR /usr/src/app
COPY --from=build /usr/src/app /usr/src/app
RUN npm ci --only=production
EXPOSE 3000
CMD ["npm", "start"]
