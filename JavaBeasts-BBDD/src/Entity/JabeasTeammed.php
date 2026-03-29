<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;

/**
 * JabeasTeammed
 *
 * @ORM\Table(name="JaBeas_Teammed", uniqueConstraints={@ORM\UniqueConstraint(name="uq_teamed_moves", columns={"Team_Id", "JaBeas_Id", "Slot"})}, indexes={@ORM\Index(name="fk_teamed_move2", columns={"Move_2"}), @ORM\Index(name="fk_teamed_move1", columns={"Move_1"}), @ORM\Index(name="fk_teamed_jabeas", columns={"JaBeas_Id"}), @ORM\Index(name="IDX_E76175E97355C590", columns={"Team_Id"})})
 * @ORM\Entity
 */
class JabeasTeammed
{
    /**
     * @var int
     *
     * @ORM\Column(name="Slot", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="NONE")
     * @Groups({"jabeasTeammed:read"})
 */
    private $slot;

    /**
     * @var Jabeas
     *
     * @ORM\ManyToOne(targetEntity="Jabeas")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="JaBeas_Id", referencedColumnName="JaBeas_Id")
     * })
     * @Groups({"jabeasTeammed:read"})
 */
    private $jabeas;

    /**
     * @var Moves
     *
     * @ORM\ManyToOne(targetEntity="Moves")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="Move_1", referencedColumnName="Move_Id")
     * })
     * @Groups({"jabeasTeammed:read"})
 */
    private $move1;

    /**
     * @var Moves
     *
     * @ORM\ManyToOne(targetEntity="Moves")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="Move_2", referencedColumnName="Move_Id")
     * })
     * @Groups({"jabeasTeammed:read"})
     */
    private $move2;

    /**
     * @var Teams
     *
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="NONE")
     * @ORM\OneToOne(targetEntity="Teams")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="Team_Id", referencedColumnName="Team_Id")
     * })
     */
    private $team;

    public function getSlot(): int
    {
        return $this->slot;
    }

    public function setSlot(int $slot): void
    {
        $this->slot = $slot;
    }

    public function getJabeas(): Jabeas
    {
        return $this->jabeas;
    }

    public function setJabeas(Jabeas $jabeas): void
    {
        $this->jabeas = $jabeas;
    }

    public function getMove1(): Moves
    {
        return $this->move1;
    }

    public function setMove1(Moves $move1): void
    {
        $this->move1 = $move1;
    }

    public function getMove2(): Moves
    {
        return $this->move2;
    }

    public function setMove2(Moves $move2): void
    {
        $this->move2 = $move2;
    }

    public function getTeam(): Teams
    {
        return $this->team;
    }

    public function setTeam(Teams $team): void
    {
        $this->team = $team;
    }



}
