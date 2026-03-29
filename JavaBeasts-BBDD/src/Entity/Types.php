<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Types
 *
 * @ORM\Table(name="Types", uniqueConstraints={@ORM\UniqueConstraint(name="Type", columns={"Type"})})
 * @ORM\Entity
 */
class Types
{
    /**
     * @var int
     *
     * @ORM\Column(name="Type_Id", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $typeId;

    /**
     * @var string
     *
     * @ORM\Column(name="Type", type="string", length=25, nullable=false)
     */
    private $type;

    /**
     * @var string|null
     *
     * @ORM\Column(name="Description", type="string", length=200, nullable=true)
     */
    private $description;

    public function getTypeId(): int
    {
        return $this->typeId;
    }

    public function getType(): string
    {
        return $this->type;
    }

    public function setType(string $type): void
    {
        $this->type = $type;
    }

    public function getDescription(): ?string
    {
        return $this->description;
    }

    public function setDescription(?string $description): void
    {
        $this->description = $description;
    }



}
