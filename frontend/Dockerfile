#1
FROM nginx:alpine

# 2
RUN apk add --no-cache curl

# 3
COPY dist/frontend/ /usr/share/nginx/html

# 4
COPY nginx.conf /etc/nginx/conf.d/default.conf