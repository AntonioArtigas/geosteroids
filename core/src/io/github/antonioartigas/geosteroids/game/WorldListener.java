package io.github.antonioartigas.geosteroids.game;

public interface WorldListener {
    void playerDied(int lives);
    void playerRespawn();

    void gameOver();
}
