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
# Copy template instead of direct conf
COPY nginx.conf.template /etc/nginx/templates/default.conf.template

# Railway dynamically assigns $PORT
EXPOSE 80

# Use envsubst to replace variables in nginx config at startup
CMD ["sh", "-c", "envsubst '${PORT} ${BACKEND_URL} ${AI_URL}' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf && sed -i 's/listen 80;/listen '\"$PORT\"';/g' /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
