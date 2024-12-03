# FROM node:18-alpine AS build
# WORKDIR /usr/src/app
# COPY package*.json ./
# RUN npm ci
# COPY . .
# RUN npm run build

# FROM nginx:stable-alpine as production
# COPY --from=build /usr/src/app/dist /usr/share/nginx/html
# EXPOSE 80

# ENTRYPOINT ["nginx", "-g", "daemon off;"] 

# # Stage 2: Serve the app with a minimal Node.js server
# FROM node:18-alpine
# WORKDIR /app
# COPY --from=build /usr/src/app/build ./build

# # Install a lightweight static file server
# RUN npm install -g serve

# # Expose the port and set the entry point
# EXPOSE 80
# CMD ["serve", "-s", "build", "-l", "80"]


# OK
FROM node:18-alpine AS build
WORKDIR /react-app
COPY package*.json ./

# Install the packages in package.json
RUN npm install

# Copy the project files from the host to the work directory in the container
COPY . /react-app/

# Expose the port in the container so that it can be accessed from the outside
EXPOSE 5173

# Command to run the app
CMD ["npm", "run", "exposed-port"]