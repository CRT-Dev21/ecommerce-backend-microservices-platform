db = db.getSiblingDB('catalog-db');

// Crear usuario para la app
db.createUser({
  user: 'products_user',
  pwd: 'products_pass',
  roles: [{ role: 'readWrite', db: 'catalog-db' }]
});

// Crear colección de categorías con datos iniciales
db.createCollection('categories');

db.categories.insertMany([
  {
    _id: "electronics",
    name: "Electrónica",
    description: "Dispositivos electrónicos y gadgets",
    icon: "laptop"
  },
  {
    _id: "clothing",
    name: "Ropa y Moda",
    description: "Ropa, calzado y accesorios",
    icon: "shirt"
  },
  {
    _id: "home",
    name: "Hogar y Jardín",
    description: "Muebles, decoración y herramientas",
    icon: "home"
  },
  {
    _id: "sports",
    name: "Deportes",
    description: "Equipamiento y ropa deportiva",
    icon: "dumbbell"
  },
  {
    _id: "books",
    name: "Libros y Educación",
    description: "Libros, cursos y material educativo",
    icon: "book"
  },
  {
    _id: "beauty",
    name: "Belleza y Salud",
    description: "Cosméticos, cuidado personal y salud",
    icon: "sparkles"
  },
  {
    _id: "toys",
    name: "Juguetes y Juegos",
    description: "Juguetes para todas las edades",
    icon: "gamepad"
  },
  {
    _id: "food",
    name: "Alimentos y Bebidas",
    description: "Productos alimenticios y bebidas",
    icon: "utensils"
  }
]);

db.createCollection('products');

db.products.createIndex(
  { name: "text", description: "text" },
  { name: "search_index" }
);

db.products.createIndex({ categoryId: 1 });