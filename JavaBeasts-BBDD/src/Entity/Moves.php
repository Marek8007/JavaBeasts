<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Moves
 *
 * @ORM\Table(name="Moves", uniqueConstraints={@ORM\UniqueConstraint(name="Name", columns={"Name"})}, indexes={@ORM\Index(name="fk_moves_unique_jabeas", columns={"Unique_JaBeas_Id"}), @ORM\Index(name="fk_moves_type", columns={"Type_Id"})})
 * @ORM\Entity
 */
class Moves
{
    /**
     * @var int
     *
     * @ORM\Column(name="Move_Id", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $moveId;

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
     * @ORM\Column(name="Damage", type="integer", nullable=false)
     */
    private $damage = '0';

    /**
     * @var string|null
     *
     * @ORM\Column(name="Special_Effect", type="string", length=200, nullable=true)
     */
    private $specialEffect;

    /**
     * @var int
     *
     * @ORM\Column(name="Accuracy", type="integer", nullable=false)
     */
    private $accuracy;

    /**
     * @var Types
     *
     * @ORM\ManyToOne(targetEntity="Types")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="Type_Id", referencedColumnName="Type_Id")
     * })
     */
    private $type;

    /**
     * @var Jabeas
     *
     * @ORM\ManyToOne(targetEntity="Jabeas")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="Unique_JaBeas_Id", referencedColumnName="JaBeas_Id")
     * })
     */
    private $uniqueJabeas;

    public function getMoveId(): int
    {
        return $this->moveId;
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

    public function getDamage(): int|string
    {
        return $this->damage;
    }

    public function setDamage(int|string $damage): void
    {
        $this->damage = $damage;
    }

    public function getSpecialEffect(): ?string
    {
        return $this->specialEffect;
    }

    public function setSpecialEffect(?string $specialEffect): void
    {
        $this->specialEffect = $specialEffect;
    }

    public function getAccuracy(): int
    {
        return $this->accuracy;
    }

    public function setAccuracy(int $accuracy): void
    {
        $this->accuracy = $accuracy;
    }

    public function getType(): Types
    {
        return $this->type;
    }

    public function setType(Types $type): void
    {
        $this->type = $type;
    }

    public function getUniqueJabeas(): Jabeas
    {
        return $this->uniqueJabeas;
    }

    public function setUniqueJabeas(Jabeas $uniqueJabeas): void
    {
        $this->uniqueJabeas = $uniqueJabeas;
    }



}
