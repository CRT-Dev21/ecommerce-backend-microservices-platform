import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Counter, Rate } from 'k6/metrics';

const homepageDuration = new Trend('homepage_duration', true);
const searchDuration   = new Trend('search_duration',   true);
const createDuration   = new Trend('create_duration',   true);

const homepageErrors = new Rate('homepage_error_rate');
const searchErrors   = new Rate('search_error_rate');
const createErrors   = new Rate('create_error_rate');

const productsCreated = new Counter('products_created_total');

export const options = {
  stages: [
    { duration: '1m',  target: 100 }, 
    { duration: '2m',  target: 200 }, 
    { duration: '2m',  target: 400 }, 
    { duration: '2m',  target: 600 }, 
    { duration: '2m',  target: 800 }, 
    { duration: '1m',  target: 0   }, 
  ],
  thresholds: {
    http_req_failed:     ['rate<0.20'],  
    homepage_duration:   ['p(95)<5000'], 
    search_duration:     ['p(95)<5000'],
    create_duration:     ['p(95)<10000'],
  },
};

const binFile = open('./imagen.png', 'b');
const TOKEN = __ENV.TOKEN || 'eyJraWQiOiJmOGJiNjIyMy0zMjc1LTQzMmQtYmUxNi03ZDliOTljMDMwY2YiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxIiwiYXVkIjoicG9zdG1hbi1jbGllbnQiLCJuYmYiOjE3NzMzNjExMTYsInNjb3BlIjpbIm9wZW5pZCIsInByb2ZpbGUiXSwicm9sZXMiOlsiVVNFUiIsIlNFTExFUiJdLCJpc3MiOiJodHRwOi8vYXV0aC1zZXJ2aWNlOjgwODAiLCJleHAiOjE3NzMzNjQ3MTYsImlhdCI6MTc3MzM2MTExNiwianRpIjoiZWUyMWQ4NTYtODcwNy00YzliLThlYWQtOTk4NjcxNzI3NDdhIiwiZW1haWwiOiJ2ZW5kZWRvckBnbWFpbC5jb20ifQ.OnL2vKKYdAu9KoY7FA-IsiuTWQGSvFXE8U_EE8hH8iIolRmEOgq44Ajh0kZpUFDJChhTGmB03uFYzSIm5hkrvqF2nV9t32J9J-E-fP_Jwn3Xe0LXXlTxFbkUrVgLSxnxI_8366YMutpMOYWsXt-stKv_Spw9tl7dbJAuP6oewNeBofd_0W8UqQon_rN1og8gjc2MJQ2zMq8MntrbauOp_cWtDroA4gm_HBm69XbWpV5uEqHZBHfE--yfxhXLN7sVKDQ_z_J3mSLq3Mc6GhbnEL_NO8DEbErs9GhH7gZjmWJL9lsGC7Sterm79wqGrKhrEVAyt2131mz7JKXpHdC89A';

const SEARCH_TERMS = [
  'Heavy', 'Load', 'Producto', 'Test', 'stress',
  'stress-test', 'ecommerce', 'sale', 'nuevo', 'oferta',
];
const CATEGORY_IDS = ['books', 'electronics', 'clothing', 'home', 'sports'];

function randomItem(arr) { return arr[Math.floor(Math.random() * arr.length)]; }
function randomPrice()   { return (Math.random() * 999 + 1).toFixed(2); }

function scenarioHomepage() {
  const limit = Math.random() > 0.5 ? 20 : 50;
  const res = http.get(
    `http://localhost:8081/products/homepage?limit=${limit}`,
    { tags: { scenario: 'homepage' }, timeout: '10s' }
  );
  homepageDuration.add(res.timings.duration);
  const ok = check(res, {
    'Homepage status 200':   (r) => r.status === 200,
    'Homepage has products': (r) => { try { return JSON.parse(r.body).length > 0; } catch { return false; } },
  });
  homepageErrors.add(!ok);
}

function scenarioSearch() {
  const term       = randomItem(SEARCH_TERMS);
  const categoryId = Math.random() > 0.5 ? randomItem(CATEGORY_IDS) : null;
  const minPrice   = Math.random() > 0.7 ? Math.floor(Math.random() * 100) : null;
  const maxPrice   = minPrice ? minPrice + Math.floor(Math.random() * 900) : null;
  const page       = Math.floor(Math.random() * 3);

  let url = `http://localhost:8081/products/search?searchTerm=${term}&page=${page}&size=20`;
  if (categoryId) url += `&categoryId=${categoryId}`;
  if (minPrice)   url += `&minPrice=${minPrice}`;
  if (maxPrice)   url += `&maxPrice=${maxPrice}`;

  const res = http.get(url, { tags: { scenario: 'search' }, timeout: '10s' });
  searchDuration.add(res.timings.duration);
  const ok = check(res, {
    'Search status 200':         (r) => r.status === 200,
    'Valid JSON search response': (r) => { try { JSON.parse(r.body); return true; } catch { return false; } },
  });
  searchErrors.add(!ok);
}

function scenarioCreate() {
  const params = {
    headers: { 'Authorization': `Bearer ${TOKEN}` },
    tags:    { scenario: 'create' },
    timeout: '15s',
  };
  const payload = {
    data: http.file(
      JSON.stringify({
        name:        `Producto Stress ${Math.random().toString(36).slice(2, 9)}`,
        description: 'breaking point test — catalog eCommerce',
        price:       parseFloat(randomPrice()),
        categoryId:  randomItem(CATEGORY_IDS),
        stock:       Math.floor(Math.random() * 100) + 1,
      }),
      'data.json', 'application/json'
    ),
    image: http.file(binFile, 'init/test-image.png', 'image/png'),
  };
  const res = http.post('http://localhost:8081/products', payload, params);
  createDuration.add(res.timings.duration);
  const ok = check(res, {
    'Create status 200/201': (r) => r.status === 200 || r.status === 201,
  });
  createErrors.add(!ok);
  if (ok) productsCreated.add(1);
}

export default function () {
  const roll = Math.random();

  if (roll < 0.70) {
    scenarioHomepage();
    sleep(0.1 + Math.random() * 0.2);
  } else if (roll < 0.90) {
    scenarioCreate();
  } else {
    scenarioSearch();
    sleep(0.1 + Math.random() * 0.2);
  }
}

export function handleSummary(data) {
  function ms(metricName, key) {
    const v = data.metrics[metricName]?.values[key];
    return v != null ? `${v.toFixed(0)}ms` : 'N/A';
  }
  function pct(metricName) {
    const v = data.metrics[metricName]?.values['rate'];
    return v != null ? `${(v * 100).toFixed(3)}%` : 'N/A';
  }
  function count(metricName) {
    return data.metrics[metricName]?.values['count'] ?? 0;
  }
  function rps(metricName) {
    const v = data.metrics[metricName]?.values['rate'];
    return v != null ? `${v.toFixed(1)} req/s` : 'N/A';
  }

  const lines = [
    '',
    '╔══════════════════════════════════════════════════════════╗',
    '║       CATALOG SERVICE — BREAKING POINT REPORT            ║',
    '║       800 VUs peak · 10 min · single instance            ║',
    '╚══════════════════════════════════════════════════════════╝',
    '',
    '┌─ GLOBAL ─────────────────────────────────────────────────',
    `│  Total requests : ${count('http_reqs')}`,
    `│  Throughput     : ${rps('http_reqs')}`,
    `│  Error rate     : ${pct('http_req_failed')}`,
    `│  p(95) global   : ${ms('http_req_duration', 'p(95)')}`,
    `│  p(90) global   : ${ms('http_req_duration', 'p(90)')}`,
    `│  avg global     : ${ms('http_req_duration', 'avg')}`,
    `│  max global     : ${ms('http_req_duration', 'max')}`,
    '',
    '┌─ HOMEPAGE — 70% load · Redis cache ──────────────────────',
    `│  med      : ${ms('homepage_duration', 'med')}`,
    `│  p(90)    : ${ms('homepage_duration', 'p(90)')}`,
    `│  p(95)    : ${ms('homepage_duration', 'p(95)')}`,
    `│  max      : ${ms('homepage_duration', 'max')}`,
    `│  avg      : ${ms('homepage_duration', 'avg')}`,
    `│  errors   : ${pct('homepage_error_rate')}`,
    '',
    '┌─ SEARCH — 10% load · MongoDB ────────────────────────────',
    `│  med      : ${ms('search_duration', 'med')}`,
    `│  p(90)    : ${ms('search_duration', 'p(90)')}`,
    `│  p(95)    : ${ms('search_duration', 'p(95)')}`,
    `│  max      : ${ms('search_duration', 'max')}`,
    `│  avg      : ${ms('search_duration', 'avg')}`,
    `│  errors   : ${pct('search_error_rate')}`,
    '',
    '┌─ CREATE PRODUCT — 20% load · CPU + Kafka + Storage ──────',
    `│  med      : ${ms('create_duration', 'med')}`,
    `│  p(90)    : ${ms('create_duration', 'p(90)')}`,
    `│  p(95)    : ${ms('create_duration', 'p(95)')}`,
    `│  max      : ${ms('create_duration', 'max')}`,
    `│  avg      : ${ms('create_duration', 'avg')}`,
    `│  errors   : ${pct('create_error_rate')}`,
    `│  created  : ${count('products_created_total')} productos`,
    '',
    '└──────────────────────────────────────────────────────────',
    '',
  ];

  lines.forEach(l => console.log(l));

  return {
    'breaking-point-summary.json': JSON.stringify(data, null, 2),
    stdout: '',
  };
}