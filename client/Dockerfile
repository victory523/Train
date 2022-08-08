FROM node:18-alpine as build-deps
WORKDIR /usr/src/app
COPY package.json yarn.lock ./
RUN yarn
COPY . ./
RUN yarn build

FROM nginx:1.21-alpine
COPY --from=build-deps /usr/src/app/dist /usr/share/nginx/html
COPY --from=build-deps /usr/src/app/nginx.conf /usr/share/nginx/nginx.conf
EXPOSE 80
CMD envsubst '$API_INTERNAL_HOST,$API_EXTERNAL_HOST,$API_INTERNAL_PORT,$API_EXTERNAL_PORT' < /usr/share/nginx/nginx.conf > /etc/nginx/conf.d/default.conf && cat /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'