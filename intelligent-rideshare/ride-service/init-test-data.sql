-- ============================================
-- Script SQL pour données de test
-- Fichier: scripts/init-test-data.sql
-- ============================================

-- Nettoyer les données existantes
TRUNCATE TABLE rides CASCADE;

-- Insérer des trajets de test
INSERT INTO rides (
    id, driver_id, driver_name, driver_rating,
    departure_city, departure_address, destination_city, destination_address,
    departure_time, distance, duration,
    total_seats, available_seats, price_per_seat,
    vehicle_make, vehicle_model, vehicle_color,
    description, status,
    allow_smoking, allow_pets, allow_luggage,
    created_at, updated_at
) VALUES
-- Trajet 1: Paris -> Lyon
(
    '550e8400-e29b-41d4-a716-446655440001',
    'user001',
    'Jean Dupont',
    4.8,
    'Paris',
    'Gare de Lyon, Paris',
    'Lyon',
    'Part-Dieu, Lyon',
    CURRENT_TIMESTAMP + INTERVAL '2 days',
    465.0,
    310,
    4,
    3,
    25.0,
    'Renault',
    'Mégane',
    'Gris',
    'Trajet régulier Paris-Lyon. Départ depuis Gare de Lyon. Climatisation, musique au choix.',
    'PUBLISHED',
    false,
    false,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),

-- Trajet 2: Paris -> Lyon (un peu plus tard)
(
    '550e8400-e29b-41d4-a716-446655440002',
    'user002',
    'Marie Martin',
    5.0,
    'Paris',
    'Porte de Versailles, Paris',
    'Lyon',
    'Perrache, Lyon',
    CURRENT_TIMESTAMP + INTERVAL '2 days' + INTERVAL '3 hours',
    465.0,
    300,
    3,
    2,
    28.0,
    'Peugeot',
    '308',
    'Bleu',
    'Départ flexible. Arrêt possible à Fontainebleau. Non-fumeur.',
    'PUBLISHED',
    false,
    true,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),

-- Trajet 3: Marseille -> Nice
(
    '550e8400-e29b-41d4-a716-446655440003',
    'user003',
    'Pierre Durand',
    4.5,
    'Marseille',
    'Gare Saint-Charles, Marseille',
    'Nice',
    'Promenade des Anglais, Nice',
    CURRENT_TIMESTAMP + INTERVAL '1 day',
    200.0,
    140,
    4,
    4,
    18.0,
    'Volkswagen',
    'Golf',
    'Noir',
    'Trajet le long de la côte. Vue sur mer garantie!',
    'PUBLISHED',
    false,
    false,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),

-- Trajet 4: Paris -> Marseille
(
    '550e8400-e29b-41d4-a716-446655440004',
    'user004',
    'Sophie Bernard',
    4.9,
    'Paris',
    'Porte d''Italie, Paris',
    'Marseille',
    'Vieux-Port, Marseille',
    CURRENT_TIMESTAMP + INTERVAL '3 days',
    775.0,
    515,
    3,
    1,
    45.0,
    'BMW',
    'Série 3',
    'Blanc',
    'Trajet confortable, véhicule récent. Pause déjeuner prévue à Lyon.',
    'PUBLISHED',
    false,
    false,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),

-- Trajet 5: Paris -> Toulouse
(
    '550e8400-e29b-41d4-a716-446655440005',
    'user005',
    'Thomas Petit',
    4.7,
    'Paris',
    'Porte d''Orléans, Paris',
    'Toulouse',
    'Capitole, Toulouse',
    CURRENT_TIMESTAMP + INTERVAL '1 day' + INTERVAL '6 hours',
    680.0,
    453,
    4,
    2,
    35.0,
    'Citroën',
    'C5',
    'Rouge',
    'Départ matinal. Trajet direct sans arrêt prolongé.',
    'PUBLISHED',
    false,
    false,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Vérifier les insertions
SELECT
    id,
    driver_name,
    departure_city,
    destination_city,
    TO_CHAR(departure_time, 'YYYY-MM-DD HH24:MI') as departure,
    available_seats,
    price_per_seat
FROM rides
ORDER BY departure_time;

-- ============================================
-- Script bash pour tester l'API
-- Fichier: scripts/test-api.sh
-- ============================================

#!/bin/bash

echo "🧪 Testing Ride Service API"
echo "================================"

BASE_URL="http://localhost:8082/api/rides"

echo ""
echo "1️⃣ Health Check"
curl -s $BASE_URL/health
echo ""

echo ""
echo "2️⃣ Search Rides: Paris -> Lyon"
curl -s "$BASE_URL/search?departure=Paris&destination=Lyon" | jq '.'

echo ""
echo "3️⃣ Get Upcoming Rides"
curl -s "$BASE_URL/upcoming" | jq '.'

echo ""
echo "4️⃣ Create New Ride"
curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "driverId": "user999",
    "driverName": "Test Driver",
    "departureCity": "Paris",
    "destinationCity": "Bordeaux",
    "departureTime": "2024-12-20T14:00:00",
    "availableSeats": 3,
    "pricePerSeat": 30.0,
    "vehicleMake": "Tesla",
    "vehicleModel": "Model 3",
    "description": "Trajet test"
  }' | jq '.'

echo ""
echo "✅ Tests completed!"


-- ============================================
-- Commandes Docker utiles
-- ============================================

-- Se connecter à PostgreSQL
docker exec -it rideshare-postgres psql -U postgres -d covoiturage_db

-- Lister les tables
\dt

-- Voir la structure de la table rides
\d rides

-- Compter les trajets
SELECT COUNT(*) FROM rides;

-- Voir les trajets disponibles
SELECT
    driver_name,
    departure_city || ' → ' || destination_city as trajet,
    TO_CHAR(departure_time, 'DD/MM à HH24:MI') as depart,
    available_seats as places,
    price_per_seat || '€' as prix
FROM rides
WHERE status = 'PUBLISHED'
ORDER BY departure_time;


-- ============================================
-- RÉSUMÉ - Structure complète des 3 microservices
-- ============================================

/*

📁 MESSAGING-GATEWAY (Port 8087)
├── MessagingGatewayApplication.java        ← Main class
├── dto/
│   ├── AIProcessRequest.java               ← Request vers AI Agent
│   ├── AIProcessResponse.java              ← Response de l'AI Agent
│   ├── QuickAction.java                    ← Boutons d'action
│   ├── ConversationContext.java            ← Contexte conversation
│   └── MessageHistory.java                 ← Historique messages
├── service/
│   ├── ConversationService.java            ← Gestion contexte (Redis)
│   └── AIAgentClient.java                  ← Client HTTP vers AI Agent
├── telegram/
│   └── TelegramBotHandler.java             ← Handler Telegram
└── config/
    └── RedisConfig.java                    ← Config Redis

COMMUNICATION:
- Reçoit messages Telegram
- Appelle AI-AGENT-SERVICE via HTTP POST /api/ai/process
- Stocke contexte dans Redis


📁 AI-AGENT-SERVICE (Port 8086)
├── AiAgentServiceApplication.java          ← Main class
├── dto/
│   ├── AIProcessRequest.java               ← Request du gateway
│   ├── AIProcessResponse.java              ← Response au gateway
│   ├── RideDTO.java                        ← Trajet (de Ride Service)
│   └── SearchRidesRequest.java             ← Request vers Ride Service
├── controller/
│   └── AIController.java                   ← API REST /api/ai/process
├── service/
│   ├── RideShareAIAgent.java               ← Agent IA principal
│   └── RideToolService.java                ← Outil: appelle Ride Service
└── config/
    └── SpringAIConfig.java                 ← Config Spring AI

COMMUNICATION:
- Reçoit HTTP POST de MESSAGING-GATEWAY
- Utilise Spring AI (OpenAI GPT-4)
- Appelle RIDE-SERVICE via HTTP GET /api/rides/search


📁 RIDE-SERVICE (Port 8082)
├── RideServiceApplication.java             ← Main class
├── entity/
│   ├── Ride.java                           ← Entité JPA
│   └── RideStatus.java                     ← Enum statuts
├── dto/
│   ├── RideDTO.java                        ← Response DTO
│   ├── SearchRidesRequest.java             ← Request search
│   ├── CreateRideRequest.java              ← Request création
│   └── CreateRideResponse.java             ← Response création
├── repository/
│   └── RideRepository.java                 ← JPA Repository
├── service/
│   ├── RideService.java                    ← Logique métier
│   └── DistanceService.java                ← Calcul distances
└── controller/
    └── RideController.java                 ← API REST

COMMUNICATION:
- Reçoit HTTP GET de AI-AGENT-SERVICE
- Interroge PostgreSQL via JPA
- Retourne les résultats en JSON


📊 FLUX COMPLET:
1. Utilisateur: "Je cherche Paris-Lyon"
   ↓ Telegram
2. MESSAGING-GATEWAY:8087
   - TelegramBotHandler
   - HTTP POST → AI-AGENT-SERVICE
   ↓
3. AI-AGENT-SERVICE:8086
   - AIController.processMessage()
   - RideShareAIAgent (Spring AI)
   - RideToolService
   - HTTP GET → RIDE-SERVICE
   ↓
4. RIDE-SERVICE:8082
   - RideController.searchRides()
   - RideService.searchRides()
   - RideRepository.searchRides()
   - SQL → PostgreSQL
   ↓
5. Retour des données
   RIDE-SERVICE → AI-AGENT → MESSAGING-GATEWAY → Telegram


🔑 POINTS CLÉS:
- Chaque service est une application Spring Boot indépendante
- Communication inter-services via HTTP/REST (RestTemplate)
- Classes @Service = logique métier DANS un microservice
- Microservice = application complète avec son propre port
- PostgreSQL partagé entre les services (simplification pour démarrer)
- Redis pour le contexte conversationnel (MESSAGING-GATEWAY)

*/