<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * JabeasHistory
 *
 * @ORM\Table(name="JaBeas_History", indexes={@ORM\Index(name="fk_jabeas_history_owner", columns={"Owner_Id"}), @ORM\Index(name="fk_jabeas_history_move2", columns={"Move_2"}), @ORM\Index(name="fk_jabeas_history_move1", columns={"Move_1"}), @ORM\Index(name="fk_jabeas_history_match", columns={"Match_Id"}), @ORM\Index(name="fk_jabeas_history_jabeas", columns={"JaBeas_Id"})})
 * @ORM\Entity
 */
class JabeasHistory
{
    /**
     * @var int
     *
     * @ORM\Column(name="JaBeas_Hitory_Id", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $jabeasHitoryId;

    /**
     * @var int
     *
     * @ORM\Column(name="Slot", type="integer", nullable=false)
     */
    private $slot;

    /**
     * @var Jabeas
     *
     * @ORM\ManyToOne(targetEntity="Jabeas")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="JaBeas_Id", referencedColumnName="JaBeas_Id")
     * })
     */
    private $jabeas;

    /**
     * @var MatchesHistory
     *
     * @ORM\ManyToOne(targetEntity="MatchesHistory")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="Match_Id", referencedColumnName="Match_Id")
     * })
     */
    private $match;

    /**
     * @var Moves
     *
     * @ORM\ManyToOne(targetEntity="Moves")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="Move_1", referencedColumnName="Move_Id")
     * })
     */
    private $move1;

    /**
     * @var Moves
     *
     * @ORM\ManyToOne(targetEntity="Moves")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="Move_2", referencedColumnName="Move_Id")
     * })
     */
    private $move2;

    /**
     * @var Users
     *
     * @ORM\ManyToOne(targetEntity="Users")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="Owner_Id", referencedColumnName="User_Id")
     * })
     */
    private $owner;

    public function getJabeasHitoryId(): int
    {
        return $this->jabeasHitoryId;
    }

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

    public function getMatch(): MatchesHistory
    {
        return $this->match;
    }

    public function setMatch(MatchesHistory $match): void
    {
        $this->match = $match;
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

    public function getOwner(): Users
    {
        return $this->owner;
    }

    public function setOwner(Users $owner): void
    {
        $this->owner = $owner;
    }



}
