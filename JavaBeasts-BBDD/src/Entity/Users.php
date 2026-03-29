<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;

/**
 * Users
 *
 * @ORM\Table(name="Users", uniqueConstraints={@ORM\UniqueConstraint(name="Username", columns={"Username"})})
 * @ORM\Entity
 */
class Users
{
    /**
     * @var int
     *
     * @ORM\Column(name="User_Id", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     * @Groups({"users:read"})
     */
    private $userId;

    /**
     * @var string
     *
     * @ORM\Column(name="Username", type="string", length=25, nullable=false)
     * @Groups({"users:write", "users:read"})
     */
    private $username;

    /**
     * @var string
     *
     * @ORM\Column(name="Password", type="string", length=255, nullable=false)
     * @Groups({"users:write"})
     */
    private $password;

    /**
     * @var int
     *
     * @ORM\Column(name="Matches_Won", type="integer", nullable=false)
     * @Groups({"users:read"})
     */
    private $matchesWon = '0';

    /**
     * @var int
     *
     * @ORM\Column(name="Matches_Lost", type="integer", nullable=false)
     * @Groups({"users:read"})
     */
    private $matchesLost = '0';

    public function getUserId(): int
    {
        return $this->userId;
    }

    public function getUsername(): string
    {
        return $this->username;
    }

    public function setUsername(string $username): void
    {
        $this->username = $username;
    }

    public function getPassword(): string
    {
        return $this->password;
    }

    public function setPassword(string $password): void
    {
        $this->password = $password;
    }

    public function getMatchesWon(): int|string
    {
        return $this->matchesWon;
    }

    public function setMatchesWon(int|string $matchesWon): void
    {
        $this->matchesWon = $matchesWon;
    }

    public function getMatchesLost(): int|string
    {
        return $this->matchesLost;
    }

    public function setMatchesLost(int|string $matchesLost): void
    {
        $this->matchesLost = $matchesLost;
    }



}
