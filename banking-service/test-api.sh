#!/bin/bash

echo "🏦 Probando Sistema Bancario Reactivo"
echo "======================================"

BASE_URL="http://localhost:8080"

echo ""
echo "1. 📊 Health Check"
curl -s "$BASE_URL/actuator/health" | echo "Health: $(cat)"

echo ""
echo "2. 📋 Listar cuentas existentes"
curl -s "$BASE_URL/api/accounts" | echo "Cuentas: $(cat)"

echo ""
echo "3. 💰 Consultar balance de cuenta ACC001"
ACCOUNT_ID="550e8400-e29b-41d4-a716-446655440000"
curl -s "$BASE_URL/api/accounts/$ACCOUNT_ID/balance" | echo "Balance: $(cat)"

echo ""
echo "4. 💸 Realizar transferencia normal ($100)"
curl -X POST "$BASE_URL/api/transactions/transfer" \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": "550e8400-e29b-41d4-a716-446655440000",
    "toAccountId": "550e8400-e29b-41d4-a716-446655440001",
    "amount": 100.00,
    "description": "Transferencia de prueba"
  }' | echo "Resultado: $(cat)"

echo ""
echo "5. 🚨 Realizar transferencia sospechosa ($6000)"
curl -X POST "$BASE_URL/api/transactions/transfer" \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": "550e8400-e29b-41d4-a716-446655440000",
    "toAccountId": "550e8400-e29b-41d4-a716-446655440001",
    "amount": 6000.00,
    "description": "Transferencia de alto monto"
  }' | echo "Resultado: $(cat)"

echo ""
echo "6. 🔍 Consultar transacciones sospechosas"
curl -s "$BASE_URL/api/fraud/suspicious" | echo "Sospechosas: $(cat)"

echo ""
echo "7. 📈 Últimas 3 transacciones"
curl -s "$BASE_URL/api/transactions/latest?limit=3" | echo "Últimas: $(cat)"

echo ""
echo "✅ Pruebas completadas!"
echo ""
echo "🌐 URLs importantes:"
echo "• Swagger UI: $BASE_URL/swagger-ui.html"
echo "• H2 Console: $BASE_URL/h2-console"
echo "• Stream SSE: $BASE_URL/api/transactions/stream" 