# scoring-service

Score de risque par IA (FR-31 à FR-33). Python / scikit-learn exposé via FastAPI (port 8090).

```bash
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --port 8090
```
