# Build stage
FROM node:20 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Production stage (Nginx)
FROM nginx:stable-alpine
COPY --from=build /app/dist /usr/share/nginx/html

# Copy template to the official Nginx template directory
# The Nginx Docker image will automatically run envsubst on files in this directory
# and output them to /etc/nginx/conf.d/
COPY nginx.conf.template /etc/nginx/templates/default.conf.template

# Railway dynamically assigns $PORT
ENV PORT=80

CMD ["nginx", "-g", "daemon off;"]
