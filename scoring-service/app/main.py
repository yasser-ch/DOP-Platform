from fastapi import FastAPI

app = FastAPI(title="scoring-service", description="Démonstrateur académique")


@app.get("/health")
def health():
    return {"status": "UP"}
