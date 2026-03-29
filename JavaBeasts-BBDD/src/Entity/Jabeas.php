<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Jabeas
 *
 * @ORM\Table(name="JaBeas", uniqueConstraints={@ORM\UniqueConstraint(name="Name", columns={"Name"})}, indexes={@ORM\Index(name="fk_jabeas_type", columns={"Type"})})
 * @ORM\Entity
 */
class Jabeas
{
    /**
     * @var int
     *
     * @ORM\Column(name="JaBeas_Id", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $jabeasId;

    /**
     * @var string
     *
     * @ORM\Column(name="Name", type="string", length=25, nullable=false)
     */
    private $name;

    /**
     * @var string
     *
     * @ORM\Column(name="Description", type="string", length=200, nullable=false)
     */
    private $description;

    /**
     * @var int
     *
     * @ORM\Column(name="Health", type="integer", nullable=false)
     */
    private $health;

    /**
     * @var int
     *
     * @ORM\Column(name="Damage", type="integer", nullable=false)
     */
    private $damage;

    /**
     * @var int
     *
     * @ORM\Column(name="Defence", type="integer", nullable=false)
     */
    private $defence;

    /**
     * @var int
     *
     * @ORM\Column(name="Speed", type="integer", nullable=false)
     */
    private $speed;

    /**
     * @var Types
     *
     * @ORM\ManyToOne(targetEntity="Types")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="Type", referencedColumnName="Type_Id")
     * })
     */
    private $type;

    public function getJabeasId(): int
    {
        return $this->jabeasId;
    }

    public function getName(): string
    {
        return $this->name;
    }

    public function setName(string $name): void
    {
        $this->name = $name;
    }

    public function getDescription(): string
    {
        return $this->description;
    }

    public function setDescription(string $description): void
    {
        $this->description = $description;
    }

    public function getHealth(): int
    {
        return $this->health;
    }

    public function setHealth(int $health): void
    {
        $this->health = $health;
    }

    public function getDamage(): int
    {
        return $this->damage;
    }

    public function setDamage(int $damage): void
    {
        $this->damage = $damage;
    }

    public function getDefence(): int
    {
        return $this->defence;
    }

    public function setDefence(int $defence): void
    {
        $this->defence = $defence;
    }

    public function getSpeed(): int
    {
        return $this->speed;
    }

    public function setSpeed(int $speed): void
    {
        $this->speed = $speed;
    }

    public function getType(): Types
    {
        return $this->type;
    }

    public function setType(Types $type): void
    {
        $this->type = $type;
    }



}
