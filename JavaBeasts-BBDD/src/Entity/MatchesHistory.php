<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * MatchesHistory
 *
 * @ORM\Table(name="Matches_History", indexes={@ORM\Index(name="fk_matches_winner", columns={"Winner_Id"}), @ORM\Index(name="fk_matches_loser", columns={"Loser_Id"})})
 * @ORM\Entity
 */
class MatchesHistory
{
    /**
     * @var int
     *
     * @ORM\Column(name="Match_Id", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $matchId;

    /**
     * @var int
     *
     * @ORM\Column(name="Turns", type="integer", nullable=false)
     */
    private $turns;

    /**
     * @var Users
     *
     * @ORM\ManyToOne(targetEntity="Users")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="Loser_Id", referencedColumnName="User_Id")
     * })
     */
    private $loser;

    /**
     * @var Users
     *
     * @ORM\ManyToOne(targetEntity="Users")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="Winner_Id", referencedColumnName="User_Id")
     * })
     */
    private $winner;

    public function getMatchId(): int
    {
        return $this->matchId;
    }

    public function getTurns(): int
    {
        return $this->turns;
    }

    public function setTurns(int $turns): void
    {
        $this->turns = $turns;
    }

    public function getLoser(): Users
    {
        return $this->loser;
    }

    public function setLoser(Users $loser): void
    {
        $this->loser = $loser;
    }

    public function getWinner(): Users
    {
        return $this->winner;
    }

    public function setWinner(Users $winner): void
    {
        $this->winner = $winner;
    }




}
